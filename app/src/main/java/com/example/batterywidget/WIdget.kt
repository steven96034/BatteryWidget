package com.example.batterywidget

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.BatteryManager
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.action.ActionParameters
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.action.ActionCallback
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.appwidget.provideContent
import androidx.glance.appwidget.updateAll
import androidx.glance.background
import androidx.glance.color.ColorProvider
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.fillMaxHeight
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.padding
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class BatteryWidget : GlanceAppWidget() {

    data class BatteryInfo(val battery: Int, val current: Int, val status: String, val remainingTime: Long)

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val batteryInfo = getBatteryInfo(context)
//        val isRunning = isAlarmRunning(context)
        val time = getTimeStamp()

        provideContent {
            BatteryWidgetContent(batteryInfo, time)
        }
    }

    @Composable
    private fun BatteryWidgetContent(batteryInfo: BatteryInfo, time: String) {

        val textStyleBig = TextStyle(fontSize = 12.sp, color = ColorProvider(Color.White, Color.White))
        val textStyleSmall = TextStyle(fontSize = 10.sp, color = ColorProvider(Color.White, Color.White))

        Row(
            modifier = GlanceModifier.fillMaxWidth().background(color = Color.DarkGray),
        ) {
            Column(
                modifier = GlanceModifier.padding(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Battery Percentage: ${batteryInfo.battery}%",
                    style = textStyleBig,
                    modifier = GlanceModifier.defaultWeight()
                )
                Text(
                    text = "Remaining Time: ${batteryInfo.remainingTime} min",
                    style = textStyleBig,
                    modifier = GlanceModifier.defaultWeight()

                )
                Text(
                    text = "Current: ${batteryInfo.current} mA",
                    style = textStyleBig,
                    modifier = GlanceModifier.defaultWeight()
                )
                Text(
                    text = "Charging?: ${batteryInfo.status}",
                    style = textStyleBig,
                    modifier = GlanceModifier.defaultWeight()
                )
                Text(
                    text = "Updated Times: $count",
                    style = textStyleBig,
                    modifier = GlanceModifier.defaultWeight()
                )

                Text(
                    text = "Last Updated Time: $time",
                    style = textStyleSmall,
                    modifier = GlanceModifier.defaultWeight()
                )
            }
            Column (
                modifier = GlanceModifier.fillMaxHeight().background(color = Color.LightGray),
                verticalAlignment = Alignment.CenterVertically,
                horizontalAlignment = Alignment.End
            ) {

                Image(
                    provider = ImageProvider(android.R.drawable.ic_menu_rotate),
                    contentDescription = "Refresh",
                    modifier = GlanceModifier.clickable(actionRunCallback<RefreshAction>()).defaultWeight()
                )
                Image(
                    provider = ImageProvider(if (isAlarmRunning) android.R.drawable.ic_media_pause else android.R.drawable.ic_media_play),
                    contentDescription = if (isAlarmRunning) "Stop" else "Start",
                    modifier = GlanceModifier.clickable(actionRunCallback<ToggleAction>()).defaultWeight()
                )
            }

        }
        Log.d("UI", "After UI Updated, $isAlarmRunning")
    }

    private fun getBatteryInfo(context: Context): BatteryInfo {
        val batteryManager = context.getSystemService(Context.BATTERY_SERVICE) as BatteryManager
        val battery = batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY)
        val remainingTime = batteryManager.computeChargeTimeRemaining() // millisecond * 1000 = second
        val current = batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CURRENT_NOW)
        val status = if (remainingTime.toInt() != 0) "Yes" else "No"
        return BatteryInfo(battery, current, status, remainingTime / 1000 / 60)
    }

    private fun getTimeStamp(): String {
        val time = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("HH:mm:ss")
        return time.format(formatter)
    }

    // **this way to check if is alarm running is not valid due to the intent may be always available, such that the return be always true**
//    fun isAlarmRunning(context: Context): Boolean {
//        val intent = Intent(context, UpdateBroadcastReceiver::class.java)
//        return PendingIntent.getBroadcast(context, 0, intent,
//            PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE) != null
//    }

    companion object {
        var isAlarmRunning by mutableStateOf(true)
        var count by mutableIntStateOf(0)
    }

    // **actionRunCallback<RefreshAction>() and actionRunCallback<ToggleAction>() need "Content" in Glance App Widget also some Glance functions,
//      such that the "Action"s may fail to function resulting the failure of Preview, then we can't use "@Preview" to check if the layout works or not** //
//    private val LocalBatteryInfo = compositionLocalOf<BatteryInfo> { error("No BatteryInfo provided") }
//    class BatteryInfoProvider: PreviewParameterProvider<BatteryWidget.BatteryInfo> {
//        override val values = sequenceOf(
//            BatteryWidget.BatteryInfo(81, 1020, "Yes", 30)
//        )
//    }
//    @Preview(showBackground = true)
//    @Composable
//    fun BatteryWidgetPreview(@PreviewParameter(BatteryInfoProvider::class) batteryInfo: BatteryInfo) {
//        CompositionLocalProvider(LocalBatteryInfo provides batteryInfo,
//            LocalSize provides DpSize(100.dp, 50.dp)) {
//            BatteryWidgetContent(batteryInfo, "00:00:00")
//        }
//
//    }

}

class BatteryWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = BatteryWidget()
}

class RefreshAction : ActionCallback {
    override suspend fun onAction(
        context: Context,
        glanceId: GlanceId,
        parameters: ActionParameters
    ) {
        BatteryWidget.count++
        Log.d("Refresh", "RefreshAction!!!")
        BatteryWidget().update(context, glanceId)
        val handler = Handler(Looper.getMainLooper())
        handler.post{
            Toast.makeText(context, "Data has updated manually.", Toast.LENGTH_SHORT).show()
        }
    }
}

class ToggleAction : ActionCallback {
    override suspend fun onAction(
        context: Context,
        glanceId: GlanceId,
        parameters: ActionParameters
    ) {
        //val isRunning = BatteryWidget().isAlarmRunning(context)
        if (BatteryWidget.isAlarmRunning) {
            cancelUpdate(context)
            Log.d("Toggle", "cancelUpdate!!!Before, ${BatteryWidget.isAlarmRunning}")
            BatteryWidget.isAlarmRunning = !BatteryWidget.isAlarmRunning
            Log.d("Toggle", "cancelUpdate!!!After, ${BatteryWidget.isAlarmRunning}")
        } else {
            scheduleUpdate(context)
            Log.d("Toggle", "scheduledUpdate!!!Before, ${BatteryWidget.isAlarmRunning}")
            BatteryWidget.isAlarmRunning = !BatteryWidget.isAlarmRunning
            Log.d("Toggle", "scheduleUpdate!!!After, ${BatteryWidget.isAlarmRunning}")
        }
        BatteryWidget.count++
        BatteryWidget().update(context, glanceId)
    }
}


private fun scheduleUpdate(context: Context) {
    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    val intent = Intent(context, UpdateBroadcastReceiver::class.java)
    val pendingIntent = PendingIntent.getBroadcast(
        context,
        0,
        intent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    alarmManager.setInexactRepeating(
        AlarmManager.ELAPSED_REALTIME,
        SystemClock.elapsedRealtime() + 30000,
        60000,
        pendingIntent
    )

    val handler = Handler(Looper.getMainLooper())
    handler.post{
        Toast.makeText(context, "Alarm has scheduled.", Toast.LENGTH_SHORT).show()
    }
}

private fun cancelUpdate(context: Context) {
    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    val intent = Intent(context, UpdateBroadcastReceiver::class.java)
    val pendingIntent = PendingIntent.getBroadcast(
        context,
        0,
        intent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )
    alarmManager.cancel(pendingIntent)
    Log.d("ALARM","ALARM HAS CANCELLED!")


    // **need to guide users to turn on the notification permission if using Toast to manifest some message,
        // also os may adjust the frequency of rapidly function the Toast message in order to improve the user experience,
    // however, even that "Snackbar" doesn't need the permission, "Snackbar" cannot be used in widget but only in APP(with Activity Context)**
    val handler = Handler(Looper.getMainLooper())
    handler.post{
        Toast.makeText(context, "Alarm has cancelled.", Toast.LENGTH_SHORT).show()
    }


}

class UpdateBroadcastReceiver : BroadcastReceiver() {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    override fun onReceive(context: Context, intent: Intent) {
        scope.launch {
            BatteryWidget.count++
            BatteryWidget().updateAll(context)
        }
    }
}
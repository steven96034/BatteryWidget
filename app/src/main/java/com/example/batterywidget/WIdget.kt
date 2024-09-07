package com.example.batterywidget

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.BatteryManager
import android.os.SystemClock
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
        val isRunning = isAlarmRunning

        provideContent {
            BatteryWidgetContent(batteryInfo)
        }
    }

    @Composable
    private fun BatteryWidgetContent(batteryInfo: BatteryInfo) {

        Row(
            modifier = GlanceModifier.fillMaxWidth().background(color = Color.DarkGray),
        ) {
            Column(
                modifier = GlanceModifier.padding(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Battery: ${batteryInfo.battery}%",
                    style = TextStyle(fontSize = 12.sp,color = ColorProvider(Color.White, Color.White)),
//                    modifier = GlanceModifier.defaultWeight()
                )
                Text(
                    text = "RemainingT: ${batteryInfo.remainingTime} min",
                    style = TextStyle(fontSize = 12.sp,color = ColorProvider(Color.White, Color.White))
                )
                Text(
                    text = "Current: ${batteryInfo.current} mA",
                    style = TextStyle(fontSize = 12.sp,color = ColorProvider(Color.White, Color.White))
                )
                Text(
                    text = "Charging?: ${batteryInfo.status}",
                    style = TextStyle(fontSize = 12.sp,color = ColorProvider(Color.White, Color.White))
                )
                Text(
                    text = "UpdateTimes: ${Attempt.count}",
                    style = TextStyle(fontSize = 10.sp,color = ColorProvider(Color.White, Color.White))
                )
                val time = LocalDateTime.now()
                val formatter = DateTimeFormatter.ofPattern("HH:mm:ss")
                val formatted = time.format(formatter)
                Text(
                    text = "LastUpTime: $formatted",
                    style = TextStyle(fontSize = 10.sp,color = ColorProvider(Color.White, Color.White))
                )
            }
            Column (
                modifier = GlanceModifier.fillMaxHeight().background(color = Color.LightGray),
                verticalAlignment = Alignment.Top,
                horizontalAlignment = Alignment.End
            ) {
                Image(
                    provider = ImageProvider(android.R.drawable.ic_menu_rotate),
                    contentDescription = "Refresh",
                    modifier = GlanceModifier.clickable(actionRunCallback<RefreshAction>())
                )
                Image(
                    provider = ImageProvider(if (isAlarmRunning) android.R.drawable.ic_media_pause else android.R.drawable.ic_media_play),
                    contentDescription = if (isAlarmRunning) "Stop" else "Start",
                    modifier = GlanceModifier.clickable(actionRunCallback<ToggleAction>())
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

//    fun isAlarmRunning(context: Context): Boolean {
//        val intent = Intent(context, UpdateBroadcastReceiver::class.java)
//        return PendingIntent.getBroadcast(context, 0, intent,
//            PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE) != null
//    }

    //private var isAlarmRunning by mutableStateOf(true)

    companion object {
        var isAlarmRunning by mutableStateOf(true)
    }
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
        Attempt.count++
        Log.d("RRR", "RefreshAction!!!")
        BatteryWidget().update(context, glanceId)
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
            Log.d("TTT", "cancelUpdate!!!Before, ${BatteryWidget.isAlarmRunning}")
            BatteryWidget.isAlarmRunning = !BatteryWidget.isAlarmRunning
            Log.d("TTT", "cancelUpdate!!!After, ${BatteryWidget.isAlarmRunning}")
        } else {
            scheduleUpdate(context)
            Log.d("TTT", "scheduledUpdate!!!Before, ${BatteryWidget.isAlarmRunning}")
            BatteryWidget.isAlarmRunning = !BatteryWidget.isAlarmRunning
            Log.d("TTT", "scheduleUpdate!!!After, ${BatteryWidget.isAlarmRunning}")
        }
        Attempt.count++
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
    Log.d("AAA","ALARM HAS CANCELLED!")

    // Toast出不來，還沒解決
//    Looper.prepare()
//    Toast.makeText(context, "鬧鐘已取消", Toast.LENGTH_SHORT).show()
//    Looper.loop()
}

class UpdateBroadcastReceiver : BroadcastReceiver() {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    override fun onReceive(context: Context, intent: Intent) {
        scope.launch {
            Attempt.count++
            BatteryWidget().updateAll(context)
        }
    }
}

object Attempt{
    var count by mutableStateOf(0)

//    val _count = MutableLiveData(0)
//    val Count: Int by LiveData<Int>.hasObservers()
//        //get() = _count
}
package com.example.batterywidget.widget

import android.content.Context
import android.os.BatteryManager
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.action.ActionParameters
import androidx.glance.action.actionParametersOf
import androidx.glance.action.actionStartActivity
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.appwidget.provideContent
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
import com.example.batterywidget.MainActivity
import com.example.batterywidget.SharedDataStore
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


/**
 * Main Widget Class
 */
class BatteryWidget : GlanceAppWidget() {

    data class BatteryInfo(
        val battery: Int,
        val current: Int,
        val status: String,
        val remainingTime: Long
    )

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val batteryInfo = getBatteryInfo(context)
        val time = getTimeStamp()
        val settingDataStore = SettingDataStore(context)
        val sharedDataStore = SharedDataStore(context)

        provideContent {
            val isRunning by settingDataStore.isAlarmRunningFlow.collectAsState(true)
            val updatedTimes by sharedDataStore.countUpdateFlow.collectAsState(0)
            val alarmInterval by sharedDataStore.alarmIntervalFlow.collectAsState(60000)
            val isUpdateTimesManifest by sharedDataStore.isUpdatedTimesManifestFlow.collectAsState(true)
            Log.d("updatedTimes", "$updatedTimes")

            BatteryWidgetContent(batteryInfo, time, updatedTimes, isRunning, alarmInterval, isUpdateTimesManifest)
        }
    }

    /**
     * View of Widget
     */
    @Composable
    private fun BatteryWidgetContent(batteryInfo: BatteryInfo, time: String, updatedTimes: Int, isRunning: Boolean, alarmInterval: Int, isUpdateTimesManifest: Boolean) {

        val textStyleBig =
            TextStyle(fontSize = 12.sp, color = ColorProvider(Color.White, Color.White))
        val textStyleSmall =
            TextStyle(fontSize = 10.sp, color = ColorProvider(Color.White, Color.White))

        Row(
            modifier = GlanceModifier.fillMaxWidth().background(color = Color.DarkGray)
        ) {
            Column(
                modifier = GlanceModifier.padding(8.dp).clickable(
                    actionStartActivity<MainActivity>()
                ),
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
                if (isUpdateTimesManifest) {
                    Text(
                        text = "Updated Times: $updatedTimes",
                        style = textStyleBig,
                        modifier = GlanceModifier.defaultWeight()
                    )
                }
                Text(
                    text = "Last Updated Time: $time",
                    style = textStyleSmall,
                    modifier = GlanceModifier.defaultWeight()
                )
            }
            Column(
                modifier = GlanceModifier.fillMaxHeight().background(color = Color.LightGray),
                verticalAlignment = Alignment.CenterVertically,
                horizontalAlignment = Alignment.End
            ) {
                Image(
                    provider = ImageProvider(android.R.drawable.ic_menu_rotate),
                    contentDescription = "Refresh",
                    modifier = GlanceModifier.clickable(actionRunCallback<RefreshAction>())
                        .defaultWeight()
                )
                Image(
                    provider = ImageProvider(if (isRunning) android.R.drawable.ic_media_pause else android.R.drawable.ic_media_play),
                    contentDescription = if (isRunning) "Stop" else "Start",
                    modifier = GlanceModifier.clickable(actionRunCallback<ToggleAction>(
                        parameters = actionParametersOf(ActionParameters.Key<Boolean>("isRunning") to isRunning, ActionParameters.Key<Int>("alarmInterval") to alarmInterval)))
                        .defaultWeight()
                )
            }
        }
        Log.d("UI", "After UI Updated, $isRunning")
    }

    private fun getBatteryInfo(context: Context): BatteryInfo {
        val batteryManager = context.getSystemService(Context.BATTERY_SERVICE) as BatteryManager
        val battery = batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY)
        val remainingTime =
            batteryManager.computeChargeTimeRemaining() // millisecond * 1000 = second
        val current = batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CURRENT_NOW)
        val status = if (remainingTime.toInt() != 0) "Yes" else "No"
        return BatteryInfo(battery, current, status, remainingTime / 1000 / 60)
    }

    private fun getTimeStamp(): String {
        val time = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("HH:mm:ss")
        return time.format(formatter)
    }

    /**
     * This way to check if is alarm running is not valid due to the intent may be always available, such that the return is always true.
     */
//    fun isAlarmRunning(context: Context): Boolean {
//        val intent = Intent(context, UpdateBroadcastReceiver::class.java)
//        return PendingIntent.getBroadcast(context, 0, intent,
//            PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE) != null
//    }

     /**
      * Record state of alarm and count the times of update.
      * Not a @Composable function/object, so "remember" can't be used to trace data.
      */
//    companion object {
//        var isAlarmRunning by mutableStateOf(true)
//        //var count by mutableIntStateOf(0)
//
//    }

    /**
     * actionRunCallback<RefreshAction>() and actionRunCallback<ToggleAction>() need "Content" in Glance App Widget also some Glance functions,
     * such that the "Action"s may fail to function resulting the failure of Preview, then we can't use "@Preview" to check if the layout works or not.
     */
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
//    }
}

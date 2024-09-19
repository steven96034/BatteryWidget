package com.example.batterywidget.widget

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.updateAll
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

/**
 * For glance widget to function and update.
 */
class BatteryWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = BatteryWidget()
}

/**
 * For alarm to function and update.
 */
class UpdateBroadcastReceiver : BroadcastReceiver() {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    override fun onReceive(context: Context, intent: Intent) {
        scope.launch {
            //BatteryWidget.count++
            val settingDataStore = SettingDataStore(context)
            settingDataStore.incrementUpdateTimes(context)

            BatteryWidget().updateAll(context)
        }
    }
}
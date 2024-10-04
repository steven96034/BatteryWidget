package com.example.batterywidget.widget

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import android.util.Log
import android.widget.Toast
import androidx.glance.GlanceId
import androidx.glance.action.ActionParameters
import androidx.glance.appwidget.action.ActionCallback
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

/**
 * Refresh Action for button.
 */
class RefreshAction : ActionCallback {
    override suspend fun onAction(
        context: Context,
        glanceId: GlanceId,
        parameters: ActionParameters
    ) {
        //BatteryWidget.count++
        incrementCount(context)

        Log.d("Refresh", "RefreshAction!!!")
        BatteryWidget().update(context, glanceId)
        val handler = Handler(Looper.getMainLooper())
        handler.post {
            Toast.makeText(context, "Data has updated manually.", Toast.LENGTH_SHORT).show()
        }
    }
}

/**
 * Toggle Action for button.
 */
class ToggleAction : ActionCallback {
    override suspend fun onAction(
        context: Context,
        glanceId: GlanceId,
        parameters: ActionParameters
    ) {
        val isRunning = parameters[ActionParameters.Key<Boolean>("isRunning")]
        val alarmInterval = parameters[ActionParameters.Key<Int>("alarmInterval")]
        val settingDataStore = SettingDataStore(context)

        if (isRunning == true) {
            cancelUpdate(context)
            Log.d("Toggle", "cancelUpdate!!!Before, $isRunning")
            settingDataStore.inverseIsAlarmRunning(context)
            Log.d("Toggle", "cancelUpdate!!!After, $isRunning")
        } else {
            if (alarmInterval != null) {
                scheduleUpdate(context, alarmInterval)
            }
            Log.d("Toggle", "scheduledUpdate!!!Before, $isRunning")
            settingDataStore.inverseIsAlarmRunning(context)
            Log.d("Toggle", "scheduleUpdate!!!After, $isRunning")
        }

        incrementCount(context)

        BatteryWidget().update(context, glanceId)
    }
}

private suspend fun incrementCount(context: Context) {
    val settingDataStore = SettingDataStore(context)
    val job = Job()
    val scope = CoroutineScope(Dispatchers.Main + job)
    scope.launch{
        settingDataStore.incrementUpdateTimes(context)
    }
}


private fun scheduleUpdate(context: Context, alarmInterval: Int) {
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
        alarmInterval.toLong(),
        pendingIntent
    )

    val handler = Handler(Looper.getMainLooper())
    handler.post {
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
    Log.d("ALARM", "ALARM HAS CANCELLED!")


    /**
     * Need to guide users to turn on the notification permission if using Toast to manifest some message,
     * also os may adjust the frequency of rapidly function the Toast message in order to improve the user experience.
     * However, even that "Snackbar" doesn't need the permission, "Snackbar" cannot be used in widget but only in APP(with Activity Context).
     */
    val handler = Handler(Looper.getMainLooper())
    handler.post {
        Toast.makeText(context, "Alarm has cancelled.", Toast.LENGTH_SHORT).show()
    }
}
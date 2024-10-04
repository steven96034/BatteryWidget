package com.example.batterywidget.widget

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "widgetDataStore")


class SettingDataStore(context: Context) {

    private object Key{
        val updateTimesCounter = intPreferencesKey("updateTimes")
        val isAlarmRunningCounter = booleanPreferencesKey("isAlarmRunning")
    }

    /**
     * For updateTimes.
     */
//    suspend fun saveUpdatedTimes(context: Context, times: Int) {
//        context.dataStore.edit { preferences ->
//            preferences[Key.updateTimesCounter] = times
//        }
//    }

    val countUpdateFlow: Flow<Int> = context.dataStore.data
        .map { preferences ->
            preferences[Key.updateTimesCounter] ?: 0
        }

    suspend fun incrementUpdateTimes(context: Context) {
        context.dataStore.edit { preferences ->
            val currentTimes = preferences[Key.updateTimesCounter] ?: 0
            preferences[Key.updateTimesCounter] = currentTimes + 1
        }
    }


    /**
     * For isAlarmRunning.
     */
    val isAlarmRunningFlow: Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            preferences[Key.isAlarmRunningCounter] ?: true
        }

    suspend fun inverseIsAlarmRunning(context: Context) {
        context.dataStore.edit { preferences ->
            val currentPref = preferences[Key.isAlarmRunningCounter] ?: true
            preferences[Key.isAlarmRunningCounter] = !currentPref
        }
    }
}

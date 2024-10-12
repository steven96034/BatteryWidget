package com.example.batterywidget

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.batterywidget.widget.dataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.sharedDataStore: DataStore<Preferences> by preferencesDataStore(name = "sharedDataStore")


class SharedDataStore (context: Context){
    private object Key{
        val alarmIntervalCounter = intPreferencesKey("alarmInterval")
        val updateTimesCounter = intPreferencesKey("updateTimes")
        val isUpdateTimesManifestCounter = booleanPreferencesKey("isUpdateTimesManifest")
        val isWidgetSimpleUIManifestCounter = booleanPreferencesKey("isWidgetUIManifest")
        val isMilliAmpereCounter = booleanPreferencesKey("isMilliAmpere")
    }

    /**
     * For alarmInterval.
     */
    val alarmIntervalFlow: Flow<Int> = context.sharedDataStore.data
        .map { preferences ->
            preferences[Key.alarmIntervalCounter] ?: 60000
        }
    suspend fun saveAlarmInterval(context: Context, interval: Int) {
        context.sharedDataStore.edit { preferences ->
            preferences[Key.alarmIntervalCounter] = interval
        }
    }

    /**
     * For updateTimes.
     */
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
    suspend fun resetUpdatedTimes(context: Context) {
        context.dataStore.edit { preferences ->
            preferences[Key.updateTimesCounter] = 0
        }
    }


    /**
     * For isUpdateTimesManifest.
     */
    val isUpdatedTimesManifestFlow: Flow<Boolean> = context.sharedDataStore.data
        .map { preferences ->
            preferences[Key.isUpdateTimesManifestCounter] ?: true
        }
    suspend fun inverseIsUpdatedTimesManifest(context: Context) {
        context.sharedDataStore.edit { preferences ->
            val currentPref = preferences[Key.isUpdateTimesManifestCounter] ?: true
            preferences[Key.isUpdateTimesManifestCounter] = !currentPref
        }
    }

    /**
     * For isWidgetUIManifest.
     */
    val isWidgetSimpleUIManifestFlow: Flow<Boolean> = context.sharedDataStore.data
        .map { preferences ->
            preferences[Key.isWidgetSimpleUIManifestCounter] ?: true
        }
    suspend fun inverseIsWidgetSimpleUIManifest(context: Context) {
        context.sharedDataStore.edit { preferences ->
            val currentPref = preferences[Key.isWidgetSimpleUIManifestCounter] ?: true
            preferences[Key.isWidgetSimpleUIManifestCounter] = !currentPref
        }
    }

    /**
     * For isMicroAmpere.
     */
    val isMilliAmpereFlow: Flow<Boolean> = context.sharedDataStore.data
        .map { preferences ->
            preferences[Key.isMilliAmpereCounter] ?: true
        }
    suspend fun saveIsMilliAmpere(context: Context, isMilliAmpere: Boolean) {
        context.sharedDataStore.edit { preferences ->
            preferences[Key.isMilliAmpereCounter] = isMilliAmpere
        }
    }
}
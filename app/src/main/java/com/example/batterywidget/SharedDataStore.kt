package com.example.batterywidget

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.sharedDataStore: DataStore<Preferences> by preferencesDataStore(name = "sharedDataStore")


class SharedDataStore (context: Context){
    private object Key{
        val alarmIntervalCounter = intPreferencesKey("alarmInterval")
    }

    val alarmIntervalFlow: Flow<Int> = context.sharedDataStore.data
        .map { preferences ->
            preferences[Key.alarmIntervalCounter] ?: 60000
        }

    suspend fun saveAlarmInterval(context: Context, interval: Int) {
        context.sharedDataStore.edit { preferences ->
            preferences[Key.alarmIntervalCounter] = interval
        }
    }
}
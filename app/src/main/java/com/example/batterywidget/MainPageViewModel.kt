package com.example.batterywidget

import android.app.Application
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

open class MainPageViewModel(application: Application, preview: Boolean = false,
                             private val batteryApiService: BatteryApi
): AndroidViewModel(application) {
    private val _batteryInfo = MutableStateFlow(BatteryInfoDataClass(0, 0.toLong(), 0, "Unknown", "Unknown", 0.0F, 0.0F, "Unknown", 0, "Unknown", "Unknown", "Unknown", 0))
    val batteryInfo: StateFlow<BatteryInfoDataClass> = _batteryInfo

    init {
        if (!preview)
            updateBatteryData()
    }

    fun updateBatteryData() {
        viewModelScope.launch {
            try {
                val result = batteryApiService.fetchBatteryInfo()
                _batteryInfo.value = result
                count++
            } catch (e: Exception){
                Log.e("MainPageViewModel", "Error fetching battery info: ${e.message}")
            }
        }
    }

    companion object {
        var count by mutableIntStateOf(0)
    }
}

open class BatteryApi(private val context: Context) {
    open fun fetchBatteryInfo(): BatteryInfoDataClass {
        return if (Build.VERSION.SDK_INT < Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            BatteryInfoCaller(context).getBatteryInfoApi31()
        } else {
            BatteryInfoCaller(context).getBatteryInfoApi34()
        }
    }
}
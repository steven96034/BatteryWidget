package com.example.batterywidget

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.os.Build
import androidx.annotation.RequiresApi

class BatteryInfoCaller(context: Context) {
    private val batteryManager = context.getSystemService(Context.BATTERY_SERVICE) as BatteryManager

    private val remainingBattery = batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY)
    private val remainingTime = batteryManager.computeChargeTimeRemaining() / 1000 / 60
    private val current = batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CURRENT_NOW)
    private val avgCurrent = batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CURRENT_AVERAGE)


    private val status = when (batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_STATUS)) {
        BatteryManager.BATTERY_STATUS_CHARGING -> "Charging"
        BatteryManager.BATTERY_STATUS_FULL -> "Full"
        BatteryManager.BATTERY_STATUS_DISCHARGING -> "Discharging"
        BatteryManager.BATTERY_STATUS_NOT_CHARGING -> "Not Charging"
        BatteryManager.BATTERY_STATUS_UNKNOWN -> "Unknown"
        else -> "Out of Spec..."
    }

    private val intent = context.registerReceiver(null, IntentFilter(Intent.ACTION_BATTERY_CHANGED))

    private val technology = intent?.getStringExtra(BatteryManager.EXTRA_TECHNOLOGY) ?: "Unknown"
    private val voltage = intent?.getIntExtra(BatteryManager.EXTRA_VOLTAGE, -1)?.toFloat()?.div(1000)
    private val temperature = intent?.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, -1)?.toFloat()?.div(10)
    @RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    val cycleCount = intent?.getIntExtra(BatteryManager.EXTRA_CYCLE_COUNT, -1)
//    private val iconID = intent?.getIntExtra(BatteryManager.EXTRA_ICON_SMALL, -1)
//    @RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
//    val chargingStatus = intent?.getIntExtra(BatteryManager.EXTRA_CHARGING_STATUS, -1)
//    @RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
//    val extraStatus = intent?.getIntExtra(BatteryManager.EXTRA_STATUS, -1)


    private val plugged = when (intent?.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1)) {
        BatteryManager.BATTERY_PLUGGED_AC -> "AC"
        BatteryManager.BATTERY_PLUGGED_USB -> "USB"
        BatteryManager.BATTERY_PLUGGED_WIRELESS -> "Wireless"
        BatteryManager.BATTERY_PLUGGED_DOCK -> "Dock"
        0 -> "Not Plugged"
        else -> "Out of Spec..."
    }

    private val health = when (intent?.getIntExtra(BatteryManager.EXTRA_HEALTH, -1)) {
        BatteryManager.BATTERY_HEALTH_COLD -> "COLD"
        BatteryManager.BATTERY_HEALTH_DEAD -> "DEAD"
        BatteryManager.BATTERY_HEALTH_GOOD -> "GOOD"
        BatteryManager.BATTERY_HEALTH_OVERHEAT -> "OVERHEAT"
        BatteryManager.BATTERY_HEALTH_OVER_VOLTAGE -> "OVER_VOLTAGE"
        BatteryManager.BATTERY_HEALTH_UNSPECIFIED_FAILURE -> "UNSPECIFIED_FAILURE"
        BatteryManager.BATTERY_HEALTH_UNKNOWN -> "UNKNOWN"
        else -> "Out of Spec..."
    }

    @RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    fun getBatteryInfoApi34(): BatteryInfoDataClass {
        return BatteryInfoDataClass(remainingBattery, remainingTime, current, status, technology, voltage, temperature, cycleCount, plugged, health, avgCurrent)
    }
    fun getBatteryInfoApi31(): BatteryInfoDataClass {
        return BatteryInfoDataClass(remainingBattery, remainingTime, current, status, technology, voltage, temperature, -1, plugged, health, avgCurrent)
    }
}



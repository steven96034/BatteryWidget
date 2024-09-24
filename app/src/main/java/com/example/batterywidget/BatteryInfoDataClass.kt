package com.example.batterywidget

data class BatteryInfoDataClass(
    val remainingBattery: Int,
    val remainingTime: Long,
    val current: Int,
    val status: String,
    val technology: String,
    val voltage: Float?,
    val temperature: Float?,
    val chargingStatus: String,
    val cycleCount: Int?,
    val extraStatus: String,
    val plugged: String,
    val health: String,
    val avgCurrent: Int
    // val iconID: Int
)

package com.example.batterywidget

data class BatteryInfoDataClass(
    val remainingBattery: Int,
    val remainingTime: Long,
    val current: Int,
    val status: String,
    val technology: String,
    val voltage: Float?,
    val temperature: Float?,
    val cycleCount: Int?,
    val plugged: String,
    val health: String,
    val avgCurrent: Int
//    val iconID: Int
//    val chargingStatus: Int?,
//    val extraStatus: Int?,
    )

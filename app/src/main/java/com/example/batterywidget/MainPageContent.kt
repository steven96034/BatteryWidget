package com.example.batterywidget

import android.content.Context
import android.os.Build
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class MainPageContent {

    @Composable
    fun Content() {
        val batteryInfo = if (isInPreview()) {
            localBatteryInfo.current
        } else {
            getBatteryInfoByVersion(LocalContext.current)
        }
        DoTopAppBar(batteryInfo)
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    private fun DoTopAppBar(batteryInfo: BatteryInfoDataClass) {
        val textDescriptionStyle = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Default, color = MaterialTheme.colorScheme.onBackground, letterSpacing = 0.25.sp)
        val textContentStyle = TextStyle(fontSize = 12.sp, fontWeight = FontWeight.Normal, fontFamily = FontFamily.Default, color = MaterialTheme.colorScheme.onBackground)
        @Composable
        fun contentText(description: String, text: String){
            Row(modifier = Modifier.padding(all = 8.dp)) {
                Column {
                    Text(text = description, style = textDescriptionStyle)

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(text = text, style = textContentStyle)
                }
            }
        }
        val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
        Scaffold(
            modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
            topBar = {
                TopAppBar(
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        titleContentColor = MaterialTheme.colorScheme.primary,
                    ),
                    title = {
                        Text(
                            text = "Battery Information"
                        )
                    },
                )
            },
        ) {
            LazyColumn(modifier = Modifier.padding(it).padding(10.dp)) {
                item {
                    contentText("Remaining Battery", "${batteryInfo.remainingBattery}%")
                }
                item {
                    contentText("Remaining Time", "${batteryInfo.remainingTime} min")
                }
                item {
                    contentText("Current", "${batteryInfo.current} mA")
                }
                item {
                    contentText("Status", batteryInfo.status)
                }
                item {
                    contentText("Technology", batteryInfo.technology)
                }
                item {
                    contentText("Voltage", "${batteryInfo.voltage} mV")
                }
                item {
                    contentText("Temperature", "${batteryInfo.temperature} ℃")
                }
                item {
                    contentText("Icon ID", "${batteryInfo.iconID}")
                }
                item {
                    contentText("Charging Status", batteryInfo.chargingStatus)
                }
                item {
                    contentText("Cycle Count", "${batteryInfo.cycleCount}")
                }
                item {
                    contentText("Extra Status", batteryInfo.extraStatus)
                }
                item {
                    contentText("Plugged", batteryInfo.plugged)
                }
                item {
                    contentText("Health", batteryInfo.health)
                }
            }
        }

    }

    private fun getBatteryInfoByVersion(context: Context): BatteryInfoDataClass {
        return if (Build.VERSION.SDK_INT < Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            BatteryInfoCaller(context).getBatteryInfoApi31()
        } else {
            BatteryInfoCaller(context).getBatteryInfoApi34()
        }
    }

    /**
     * Below for Preview
     */
    private val localBatteryInfo = compositionLocalOf { BatteryInfoDataClass(0, 0.toLong(), 0, "Unknown", "Unknown", 0, 0, 0, "Unknown", 0, "Unknown", "Unknown", "Unknown") }
    @Composable
    fun isInPreview(): Boolean {
        return LocalInspectionMode.current
    }
    
    @Preview(showBackground = true)
    @Composable
    fun BatteryPreview() {
        Content()
    }
}
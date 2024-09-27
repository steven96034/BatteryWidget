package com.example.batterywidget

import android.app.Application
import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch


class MainPageContent {

    @Composable
    fun Content() {
        val currentContext = LocalContext.current
        val viewModel = remember {
            MainPageViewModel(currentContext.applicationContext as Application, batteryApiService = BatteryApi(currentContext))
        }

        DoTopAppBar(viewModel)
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    private fun DoTopAppBar(viewModel: MainPageViewModel) {

        val batteryInfo by viewModel.batteryInfo.collectAsState()

        var expandedMenu by remember { mutableStateOf(false) }
        val scope = rememberCoroutineScope()

        val textDescriptionStyle = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Default, color = MaterialTheme.colorScheme.onBackground, letterSpacing = 0.25.sp)
        val textContentStyle = TextStyle(fontSize = 12.sp, fontWeight = FontWeight.Normal, fontFamily = FontFamily.Default, color = MaterialTheme.colorScheme.onBackground)
        @Composable
        fun contentText(description: String, value: String){
            Row(modifier = Modifier.padding(all = 8.dp)) {
                Column {
                    Text(text = description, style = textDescriptionStyle)

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(text = value, style = textContentStyle)
                }
            }
        }
        val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())
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
                    scrollBehavior = scrollBehavior,
                    actions = {
                        Box {
                            IconButton(onClick = { expandedMenu = true }) {
                                Icon(
                                    imageVector = Icons.Default.MoreVert,
                                    contentDescription = "Localized description"
                                )
                            }
                            DropdownMenu(
                                expanded = expandedMenu,
                                onDismissRequest = { expandedMenu = false }
                            ) {
                                DropdownMenuItem(
                                    text = { Text("Widget Settings")},
                                    onClick = {
                                        /**
                                         * Handle settings click
                                         */
                                        scope.launch {

                                        }
                                        expandedMenu = false
                                    }
                                )
                                DropdownMenuItem(
                                    text = { Text("About This App") },
                                    onClick = {
                                        /**
                                        *Handle about click
                                        */
                                        scope.launch {

                                        }
                                        expandedMenu = false
                                    }
                                )
                            }
                        }
                    }
                )
            },
            floatingActionButtonPosition = FabPosition.End,
            floatingActionButton = {
                FloatingActionButton(onClick = { viewModel.updateBatteryData() },
                    elevation = FloatingActionButtonDefaults.bottomAppBarFabElevation())
            { Image(imageVector = Icons.Filled.Refresh, contentDescription = "Refresh") } },
            content = { innerPadding ->
                LazyColumn(modifier = Modifier.padding(10.dp), contentPadding = innerPadding) {
                    item {
                        contentText("Status", batteryInfo.status)
                    }
                    item {
                        contentText("Power Source", batteryInfo.plugged)
                    }
                    item {
                        contentText("Remaining Battery", "${batteryInfo.remainingBattery}%")
                    }
                    item {
                        contentText("Remaining Time for Charging", "${batteryInfo.remainingTime} min")
                    }
                    item {
                        contentText("Instant Current (Charging(+)/Discharging(-))", "${batteryInfo.current} mA")
                    }
                    item {
                        contentText("Average Current", "${batteryInfo.avgCurrent} mA")
                    }
                    item {
                        contentText("Technology", batteryInfo.technology)
                    }
                    item {
                        contentText("Voltage", "${batteryInfo.voltage} V")
                    }
                    item {
                        contentText("Temperature", "${batteryInfo.temperature} ℃")
                    }
                    item {
                        contentText("Health", batteryInfo.health)
                    }
                    item {
                        contentText("Cycle Count", "${batteryInfo.cycleCount}")
                    }
                    item {
                        contentText("Charging Status", batteryInfo.chargingStatus)
                    }
                    item {
                        contentText("Extra Status", batteryInfo.extraStatus)
                    }
                    item {
                        contentText("Update Times", "${MainPageViewModel.count}")
                    }
                }
            }
        )
    }


    /**
     * Below for Preview
     */

    @Preview
    @Composable
    fun BatteryAppPreview() {
        val viewModel = MainPageViewModel(application = Application(),
            preview = true,
            MockBatteryApiService(LocalContext.current)
        )

        DoTopAppBar(viewModel)
    }

    private class MockBatteryApiService(context: Context) : BatteryApi(context) {
        override fun fetchBatteryInfo(): BatteryInfoDataClass {
            return BatteryInfoDataClass(0, 0.toLong(), 0, "Unknown", "Unknown", 0.0F, 0.0F, "Unknown", 0, "Unknown", "Unknown", "Unknown", 0)
        }
    }
}

package com.example.batterywidget

import android.app.Application
import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.NotificationManagerCompat
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.launch


class MainPageContent {

    @Composable
    fun Content() {
        val currentContext = LocalContext.current
        val viewModel = remember {
            MainPageViewModel(currentContext.applicationContext as Application, batteryApiService = BatteryApi(currentContext))
        }
        val snackbarHostState = remember { SnackbarHostState() }
        LaunchedEffect(Unit) {
            if (!NotificationManagerCompat.from(currentContext).areNotificationsEnabled()) {
                snackbarHostState.showSnackbar(message = "Notification permission is not granted.\nPlease grant it in settings for better experience in widget.", duration = SnackbarDuration.Short)
            }
        }

        DoTopAppBar(viewModel)
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    private fun DoTopAppBar(viewModel: MainPageViewModel) {
        var expandedMenu by remember { mutableStateOf(false) }
        val navController = rememberNavController()
        val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route
        val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())
        val snackbarHostState = remember { SnackbarHostState() }
        val scope = rememberCoroutineScope()

        Scaffold(
            snackbarHost = { SnackbarHost(snackbarHostState) },
            modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
            topBar = {
                TopAppBar(
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        titleContentColor = MaterialTheme.colorScheme.primary,
                    ),
                    title = {
                        val titleName = when (currentRoute) {
                            "WidgetSettings" -> "Widget Settings"
                            "AboutThisApp" -> "About This App"
                            else -> "Battery Information"
                        }
                        Text(text = titleName,
                            style = MaterialTheme.typography.titleLarge, fontFamily = FontFamily.Default, color = MaterialTheme.colorScheme.primary, modifier = Modifier.padding(start = 4.dp), fontSize = 20.sp, fontWeight = FontWeight.Bold, letterSpacing = 0.25.sp, lineHeight = 30.sp
                        )
                    },
                    navigationIcon = {
                        if (currentRoute != "Home") {
                            IconButton(onClick = {
                                navController.popBackStack()
                            }) {
                                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                            }
                        }
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
                                    modifier = Modifier.padding(4.dp),
                                    onClick = {
                                        navController.navigate("WidgetSettings")
                                        expandedMenu = false
                                    }
                                )
                                DropdownMenuItem(
                                    text = { Text("About This App") },
                                    modifier = Modifier.padding(4.dp),
                                    onClick = {
                                        navController.navigate("AboutThisApp")
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
                if (currentRoute == "Home"){
                    FloatingActionButton(onClick = {
                        viewModel.updateBatteryData()
                        scope.launch {
                            snackbarHostState.showSnackbar(message = "Battery Information Updated", duration = SnackbarDuration.Short)
                        } },
                        elevation = FloatingActionButtonDefaults.bottomAppBarFabElevation())
                    { Image(imageVector = Icons.Filled.Refresh, contentDescription = "Refresh") }
                }
            },
            content = { innerPadding ->
                NavigationHost(navController, innerPadding, viewModel)
            }
        )
    }

    // Navigation host settings.
    @Composable
    fun NavigationHost(navController: NavHostController, innerPadding: PaddingValues, viewModel: MainPageViewModel){
        NavHost(navController= navController, startDestination = "Home") {
            composable("Home") {
                HomeScreen(viewModel, innerPadding)
            }
            composable("WidgetSettings") {
                WidgetSettingsScreen(innerPadding)
            }
            composable("AboutThisApp") {
                AboutThisAppScreen(innerPadding)
            }
        }
    }

    // Normal text content style.
    @Composable
    fun ContentText(description: String, value: String){
        Row(modifier = Modifier.padding(all = 8.dp)) {
            Column (modifier = Modifier.fillMaxWidth().padding(4.dp)){
                Text(text = description, fontSize = 16.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Default, color = MaterialTheme.colorScheme.onBackground, letterSpacing = 0.25.sp)

                Spacer(modifier = Modifier.height(2.dp))

                Text(text = value, fontSize = 12.sp, fontWeight = FontWeight.Normal, fontFamily = FontFamily.Default, color = MaterialTheme.colorScheme.onBackground)
            }
        }
    }

    @Composable
    fun HomeScreen(viewModel: MainPageViewModel, innerPadding: PaddingValues){
        val batteryInfo by viewModel.batteryInfo.collectAsState()
        Box(modifier = Modifier.padding(innerPadding).fillMaxSize()) {
            LazyColumn(modifier = Modifier.padding(6.dp)) {
                item {
                    ContentText("Status", batteryInfo.status)
                    ContentText("Power Source", batteryInfo.plugged)
                    ContentText("Remaining Battery", "${batteryInfo.remainingBattery}%")
                    ContentText("Remaining Time for Charging", "${batteryInfo.remainingTime} min")
                    ContentText("Instant Current", "${batteryInfo.current} mA")
                    ContentText("Average Current", "${batteryInfo.avgCurrent} mA")
                    ContentText("Technology", batteryInfo.technology)
                    ContentText("Voltage", "${batteryInfo.voltage} V")
                    ContentText("Temperature", "${batteryInfo.temperature} ℃")
                    ContentText("Health", batteryInfo.health)
                    ContentText("Cycle Count", "${batteryInfo.cycleCount}")
                    ContentText("Update Times", "${MainPageViewModel.count}")
                }
                item {
                    Row (modifier = Modifier.padding(all = 8.dp), verticalAlignment = Alignment.Bottom){
                        Column (modifier = Modifier.align(Alignment.Top).padding(vertical = 6.dp)){
                            Icon(Icons.Filled.Info, contentDescription = "Info")
                        }
                        Spacer(modifier = Modifier.width(ButtonDefaults.IconSpacing))
                        Column {
                            Text(
                                text = """For Current Information: "+" means charging, "-" means discharging. """,
                                modifier = Modifier.padding(all = 4.dp),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Light,
                                fontFamily = FontFamily.Default,
                                color = MaterialTheme.colorScheme.onBackground,
                                letterSpacing = 0.2.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = """"Cycle count" can be provided over Android 14 and above, also if phone manufacturer provides this information, it will be displayed non-zero.""",
                                modifier = Modifier.padding(all = 4.dp),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Light,
                                fontFamily = FontFamily.Default,
                                color = MaterialTheme.colorScheme.onBackground,
                                letterSpacing = 0.2.sp
                            )
                            Spacer(modifier = Modifier.height(20.dp))
                        }
                    }
                }
            }

        }
    }


    @Composable
    fun SimpleTextLine(text: String) {
        Text(text = text, modifier = Modifier.padding(all = 8.dp), fontSize = 16.sp, fontWeight = FontWeight.Normal, fontFamily = FontFamily.Default, color = MaterialTheme.colorScheme.onBackground, letterSpacing = 0.2.sp)
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun WidgetSettingsScreen(innerPadding: PaddingValues) {
        val context = LocalContext.current
        val sharedDataStore = SharedDataStore(context)
        val alarmInterval by sharedDataStore.alarmIntervalFlow.collectAsState(60000)
        var expandedMenu by remember { mutableStateOf(false) }
        val options = listOf("1", "2", "3", "4", "5")
        var selectedOrInputOption by remember { mutableStateOf(options[0]) }
        val scope = rememberCoroutineScope()

        LazyColumn (modifier = Modifier.padding(6.dp), contentPadding = innerPadding) {
            // Still have some problems on type-in part (when readOnly = true)
            item {
                SimpleTextLine("Alarm Interval for Widget Update: ${alarmInterval/60000} minutes.")
                ExposedDropdownMenuBox (expanded = expandedMenu, onExpandedChange = { expandedMenu = it}) {
                    OutlinedTextField(
                        value = selectedOrInputOption,
                        readOnly = false,
                        onValueChange = {selectedOrInputOption = it },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedMenu) },
                        modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryEditable, enabled = true).fillMaxWidth(),
                        label = { Text("Select or type-in number for alarm interval.") },
                        suffix = { Text("minutes.")},
                    )
                    ExposedDropdownMenu(expanded = expandedMenu, onDismissRequest = { expandedMenu = false }) {
                        options.forEach { option ->
                            DropdownMenuItem(text = {Text(option)}, onClick = {
                                expandedMenu = false
                                selectedOrInputOption = option
                                scope.launch {
                                    sharedDataStore.saveAlarmInterval(
                                        context = context,
                                        interval = selectedOrInputOption.toInt() * 60000
                                    )
                                }
                            })

                    } }
                }
            }
            item {
                Text(
                    text = "Alarm Interval for Widget Update should be set over or equal to ONE minute.",
                    modifier = Modifier.padding(all = 4.dp),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Light,
                    fontFamily = FontFamily.Default,
                    color = MaterialTheme.colorScheme.onBackground,
                    letterSpacing = 0.2.sp
                )
            }
        }

    }

    @Composable
    fun AboutThisAppScreen(innerPadding: PaddingValues) {
        val url = "https://github.com/steven96034/BatteryWidget"
        val uriHandler = LocalUriHandler.current

        LazyColumn (modifier = Modifier.padding(6.dp), contentPadding = innerPadding) {
            item {
                SimpleTextLine("This is the my FIRST Android app work!")
                SimpleTextLine("I had it all arranged to design a battery widget that can update battery information in real time.")
                SimpleTextLine("However, I have done all this app by the way and tried to design this more complete, also discover some cool new things in glance, compose UI, scaffold, navigation, DataStore, BatteryManager, and more.")
                SimpleTextLine("I spend plenty of time but got lots of fun in this project! Hope you enjoy it!")
                SimpleTextLine("For more information on my Github: ")
                Text(text = url, modifier = Modifier.padding(horizontal = 8.dp).clickable { uriHandler.openUri(url) }, fontSize = 16.sp, fontWeight = FontWeight.Normal, fontFamily = FontFamily.Default, color = MaterialTheme.colorScheme.secondary, letterSpacing = 0.2.sp, textDecoration = TextDecoration.Underline)
            }
        }

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
            return BatteryInfoDataClass(0, 0.toLong(), 0, "Unknown", "Unknown", 0.0F, 0.0F, 0, "Unknown", "Unknown", 0)
        }
    }
}

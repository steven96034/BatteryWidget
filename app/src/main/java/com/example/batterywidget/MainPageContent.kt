package com.example.batterywidget

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
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
import androidx.compose.ui.composed
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
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

        /**
         *  Set a modifier for onTap to hide keyboard and clear focus.
         */
        fun Modifier.autoCloseKeyboardClearFocus(): Modifier = composed {
            val keyBoardController = LocalSoftwareKeyboardController.current
            val focusManager = LocalFocusManager.current
            pointerInput(this) {
                detectTapGestures(onTap = {
                    keyBoardController?.hide()
                    focusManager.clearFocus()
                })
            }
        }

        /**
         *  Main Scaffold for content.
         */
        Scaffold(
            snackbarHost = { SnackbarHost(snackbarHostState) },
            modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection).autoCloseKeyboardClearFocus(),
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
                    actions = { if (currentRoute == "Home") {
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
                    } }
                )
            },
            floatingActionButtonPosition = FabPosition.End,
            floatingActionButton = {
                if (currentRoute == "Home"){
                    FloatingActionButton(onClick = {
                        viewModel.updateBatteryData()
                        scope.launch {
                            snackbarHostState.showSnackbar(message = "Battery Information Updated", duration = SnackbarDuration.Short)
                        }
                                                   },
                        elevation = FloatingActionButtonDefaults.bottomAppBarFabElevation()
                    ) {
                        Image(imageVector = Icons.Filled.Refresh, contentDescription = "Refresh")
                    }
                }
            },
            content = { innerPadding ->
                NavigationHost(navController, innerPadding, viewModel)
            }
        )
    }

    /**
     * Navigation host settings.
     */
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

    /**
     * Icon with normal text and description.
     */
    @Composable
    fun ContentText(description: String, icon: Int, value: String){
        Card (elevation = CardDefaults.cardElevation(defaultElevation = 4.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant), modifier = Modifier.padding(4.dp)) {
            Row(modifier = Modifier.padding(all = 8.dp)) {
                Icon(painterResource(icon), contentDescription = "Icon", modifier = Modifier.align(Alignment.CenterVertically))
                Spacer(modifier = Modifier.width(ButtonDefaults.IconSpacing))
                Column(modifier = Modifier.fillMaxWidth().padding(4.dp)) {
                    Text(
                        text = description,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Default,
                        color = MaterialTheme.colorScheme.onBackground,
                        letterSpacing = 0.25.sp
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = value,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Normal,
                        fontFamily = FontFamily.Default,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
            }
        }
    }

    /**
     * Text for some less important information.
     */
    @Composable
    fun NoteText(text: String) {
        Text(
            text = text,
            modifier = Modifier.padding(all = 4.dp),
            fontSize = 12.sp,
            fontWeight = FontWeight.Light,
            fontFamily = FontFamily.Default,
            color = MaterialTheme.colorScheme.onBackground,
            letterSpacing = 0.2.sp
        )
    }

    /**
     * Home Screen content.
     */
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun HomeScreen(viewModel: MainPageViewModel, innerPadding: PaddingValues){
        val batteryInfo by viewModel.batteryInfo.collectAsState()
        val sheetState = rememberModalBottomSheetState()
        val scope = rememberCoroutineScope()
        var showBottomSheet by remember { mutableStateOf(false) }
        val context = LocalContext.current
        val sharedDataStore = SharedDataStore(context)
        val isMilliAmpere by sharedDataStore.isMilliAmpereFlow.collectAsState(true)

        Box(modifier = Modifier.padding(innerPadding).fillMaxSize()) {
            LazyColumn(modifier = Modifier.padding(6.dp)) {
                item {
                    ContentText("Status", R.drawable.status_icon, batteryInfo.status)
                    ContentText("Power Source", R.drawable.power_source_icon, batteryInfo.plugged)
                    ContentText("Remaining Battery", R.drawable.remaining_battery_icon, "${batteryInfo.remainingBattery}%")
                    ContentText("Remaining Time for Charging", R.drawable.remaining_time_for_charging_icon, "${batteryInfo.remainingTime} min")
                    ContentText("Instant Current", R.drawable.current_icon, if(isMilliAmpere) "${batteryInfo.current} mA" else "${batteryInfo.current} μA")
                    ContentText("Average Current", R.drawable.current_icon, if(isMilliAmpere) "${batteryInfo.avgCurrent} mA" else "${batteryInfo.avgCurrent} μA")
                    ContentText("Technology", R.drawable.technology_icon, batteryInfo.technology)
                    ContentText("Voltage", R.drawable.voltage_icon, "${batteryInfo.voltage} V")
                    ContentText("Temperature", R.drawable.temperature_icon, "${batteryInfo.temperature} ℃")
                    ContentText("Health", R.drawable.health_icon, batteryInfo.health)
                    ContentText("Cycle Count", R.drawable.cycle_count_icon, "${batteryInfo.cycleCount}")
                    ContentText("Update Times", R.drawable.update_times_icon, "${MainPageViewModel.count}")
                }
                item {
                    Row (modifier = Modifier.padding(all = 8.dp), verticalAlignment = Alignment.Bottom){
                        Column (modifier = Modifier.align(Alignment.Top).padding(vertical = 6.dp)){
                            Icon(Icons.Filled.Info, contentDescription = "Info")
                        }
                        Spacer(modifier = Modifier.width(ButtonDefaults.IconSpacing))
                        Column {
                            val chooseCurrentUnitString = buildAnnotatedString {
                                append("If your current unit is wrong, ")
                                withStyle(SpanStyle(textDecoration = TextDecoration.Underline)) {
                                    pushStringAnnotation(tag = "clickable", annotation = "click me")
                                    append("click me")
                                    pop()
                                }
                                append(" to choose.")
                            }
                            Text(text = chooseCurrentUnitString,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Light,
                                fontFamily = FontFamily.Default,
                                color = MaterialTheme.colorScheme.onBackground,
                                letterSpacing = 0.2.sp,
                                modifier = Modifier.padding(all = 4.dp).clickable {
                                chooseCurrentUnitString.getStringAnnotations("clickable", 32, 39).firstOrNull()?.let {
                                    showBottomSheet = true
                                }
                            })
                            if (showBottomSheet) {
                                ModalBottomSheet(onDismissRequest = { showBottomSheet = false }, sheetState = sheetState, modifier = Modifier.padding(start = 24.dp, end = 24.dp, top = 8.dp)) {
                                    Column(modifier = Modifier.padding(4.dp)) {
                                        Text(if (isMilliAmpere) "Choose Current Unit (mA for now)" else "Choose Current Unit (μA for now)", modifier = Modifier.align(Alignment.CenterHorizontally))
                                        Spacer(modifier = Modifier.height(16.dp))
                                        Row (modifier = Modifier.padding(4.dp), horizontalArrangement = Arrangement.SpaceBetween){
                                            Button(modifier = Modifier.weight(1f), onClick = {
                                                scope.launch {
                                                    sharedDataStore.saveIsMilliAmpere(context, true)
                                                    showBottomSheet = false
                                                }
                                            }){ Text("Milliampere (mA)") }
                                            Button(modifier = Modifier.weight(1f), onClick = {
                                                scope.launch {
                                                    sharedDataStore.saveIsMilliAmpere(context, false)
                                                    showBottomSheet = false
                                                }
                                            }){ Text("Microampere (μA)") }
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(4.dp))
                            NoteText("""For Current Information: "+" means charging, "-" means discharging. """)
                            Spacer(modifier = Modifier.height(4.dp))
                            NoteText(""""Cycle count" can be provided over Android 14 and above, also if phone manufacturer provides this information, it will be displayed non-zero.""")
                            Spacer(modifier = Modifier.height(20.dp))
                        }
                    }
                }
            }

        }
    }

    /**
     * Simple text style.
     */
    @Composable
    fun SimpleText(text: String) {
        Text(text = text,
            modifier = Modifier.padding(all = 8.dp),
            fontSize = 16.sp,
            fontWeight = FontWeight.Normal,
            fontFamily = FontFamily.Default,
            color = MaterialTheme.colorScheme.onBackground,
            letterSpacing = 0.2.sp)
    }
    @Composable
    fun SimpleLightText(text: String) {
        Text(text = text,
            modifier = Modifier.padding(all = 8.dp),
            fontSize = 14.sp,
            fontWeight = FontWeight.Light,
            fontFamily = FontFamily.Default,
            color = MaterialTheme.colorScheme.onBackground,
            letterSpacing = 0.15.sp)
    }

    /**
     * NoteText with index.
     */
    @Composable
    fun IndexedNoteText(index: Int, text: String) {
        Row {
            NoteText("$index.")
            NoteText(text)
        }
    }

    /**
     * Widget Settings Screen content.
     */
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun WidgetSettingsScreen(innerPadding: PaddingValues) {
        val context = LocalContext.current
        val sharedDataStore = SharedDataStore(context)
        val alarmInterval by sharedDataStore.alarmIntervalFlow.collectAsState(60000)
        val updatedTimes by sharedDataStore.countUpdateFlow.collectAsState(0)
        val isUpdatedTimesManifest by sharedDataStore.isUpdatedTimesManifestFlow.collectAsState(true)
        val isWidgetSimpleUIManifest by sharedDataStore.isWidgetSimpleUIManifestFlow.collectAsState(true)

        var expandedMenu by remember { mutableStateOf(false) }
        val options = listOf("1", "1.5", "2", "3", "5")
        var selectedOrInputOption by remember { mutableStateOf(options[0]) }
        var isError by remember { mutableStateOf(false) }

        val scope = rememberCoroutineScope()
        val focusManager = LocalFocusManager.current

        fun validateInput(input: String) {
            if (input.isNotEmpty() && !(input.toIntOrNull() == null && input.toDoubleOrNull() == null)) {
                try {
                    val checkRange = input.toDouble()
                    isError = checkRange < 1 || checkRange > 35791
                } catch (e: NumberFormatException) {
                    isError = true
                    Log.e("NumberFormatException", "$e !!!")
                }
            } else {
                isError = true
            }
        }


        LazyColumn (modifier = Modifier.padding(6.dp).fillMaxSize(), contentPadding = innerPadding) {
            item {
                Row(modifier = Modifier.padding(top = 12.dp)) {
                    Icon(painterResource(R.drawable.alarm_icon), contentDescription = "Alarm Icon", modifier = Modifier.padding(top = 6.dp))
                    val intervalString = buildAnnotatedString {
                        append("Alarm Interval for Widget Update: ")
                        withStyle(SpanStyle(textDecoration = TextDecoration.Underline)) {append("${alarmInterval.toDouble()/60000.0}")}
                        append(" minutes.")
                    }
                    Text(text = intervalString,
                        modifier = Modifier.padding(all = 8.dp),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Normal,
                        fontFamily = FontFamily.Default,
                        color = MaterialTheme.colorScheme.onBackground,
                        letterSpacing = 0.2.sp
                    )
                }
                ExposedDropdownMenuBox (
                    expanded = expandedMenu,
                    onExpandedChange = { expandedMenu = it },
                    modifier = Modifier.padding(start = 24.dp)
                ) {
                    TextField(
                        value = selectedOrInputOption,
                        readOnly = false,
                        onValueChange = {
                            selectedOrInputOption = it
                            validateInput(it)
                            if (!isError) {
                                scope.launch {
                                    Log.d("selectedOrInputOption", selectedOrInputOption)
                                    sharedDataStore.saveAlarmInterval(
                                        context = context,
                                        interval = (selectedOrInputOption.toDouble() * 60000).toInt()
                                    )
                                }
                            }
                                        },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedMenu) },
                        singleLine = true,
                        modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryEditable, enabled = true).fillMaxWidth().padding(end = 16.dp),
                        label = {
                            if (!isError) Text("Select or type-in for alarm interval.")
                                else Text(text = "Invalid input!", color = MaterialTheme.colorScheme.error)
                        },
                        suffix = { Text("minutes.") },
                        isError = isError,
                        keyboardActions = KeyboardActions (onDone = {
                            focusManager.clearFocus()
                            expandedMenu = false
                            if (!isError) {
                                scope.launch {
                                    Log.d("selectedOrInputOption", selectedOrInputOption)
                                    sharedDataStore.saveAlarmInterval(
                                        context = context,
                                        interval = (selectedOrInputOption.toDouble() * 60000).toInt()
                                    )
                                }
                            } }
                        ),
                        keyboardOptions = KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.Number)
                    )
                    ExposedDropdownMenu(
                        expanded = expandedMenu,
                        onDismissRequest = { expandedMenu = false })
                    {
                        options.forEach { option ->
                            DropdownMenuItem(text = { Text(option) }, onClick = {
                                expandedMenu = false
                                selectedOrInputOption = option
                                isError = false
                                scope.launch {
                                    sharedDataStore.saveAlarmInterval(
                                        context = context,
                                        interval = (selectedOrInputOption.toDouble() * 60000).toInt()
                                    )
                                }
                            })
                        }
                    }
                }
            }
            item {
                Text(
                    text = "Alarm Interval for Widget Update should be set over or equal to 1 minute and should not be too long.",
                    modifier = Modifier.padding(all = 4.dp).padding(start = 24.dp),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Light,
                    fontFamily = FontFamily.Default,
                    color = MaterialTheme.colorScheme.onBackground,
                    letterSpacing = 0.2.sp
                )
                HorizontalDivider(modifier = Modifier.padding(all = 4.dp).padding(top = 16.dp, bottom = 16.dp))
            }
            item {
                Row(modifier = Modifier.padding(top = 4.dp)) {
                    Icon(Icons.Filled.Build, contentDescription = "Updated Times Setting", modifier = Modifier.padding(top = 6.dp))
                    val updatedTimesString = buildAnnotatedString {
                        append("Updated Times: ")
                        withStyle(SpanStyle(textDecoration = TextDecoration.Underline)) {append("$updatedTimes")}
                        append(" times.")}
                    Text(text = updatedTimesString,
                        modifier = Modifier.padding(all = 8.dp),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Normal,
                        fontFamily = FontFamily.Default,
                        color = MaterialTheme.colorScheme.onBackground,
                        letterSpacing = 0.2.sp
                    )
                }
                Row(modifier = Modifier.padding(start = 24.dp)) {
                    SimpleLightText("Recount updated times.")
                    ElevatedButton(onClick = {
                        scope.launch {
                            sharedDataStore.resetUpdatedTimes(context)
                        }
                                             },
                        modifier = Modifier.height(35.dp),
                        elevation = ButtonDefaults.buttonElevation(pressedElevation = 4.dp),
                        shape = MaterialTheme.shapes.large,
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                    ) { }
                }
                Row(modifier = Modifier.padding(start = 24.dp)) {
                    SimpleLightText("Manifest updated times.")
                    Switch(
                        checked = isUpdatedTimesManifest,
                        onCheckedChange = {
                            scope.launch {
                                sharedDataStore.inverseIsUpdatedTimesManifest(context)
                            }
                                          },
                        )
                }
            }
            item {
                HorizontalDivider(modifier = Modifier.padding(all = 4.dp).padding(top = 16.dp, bottom = 16.dp))
                Row(modifier = Modifier.padding(top = 4.dp)) {
                    Column (modifier = Modifier.weight(3f)){
                        Row {
                            Icon(
                                painterResource(R.drawable.is_widget_simple_ui_manifest_icon),
                                contentDescription = "Widget UI Settings",
                                modifier = Modifier.padding(top = 6.dp)
                            )
                            val isWidgetUIManifestString = buildAnnotatedString {
                                append("Simple UI mode for widget\n(when not charging): ")
                                withStyle(SpanStyle(textDecoration = TextDecoration.Underline)) {
                                    append(
                                        if (isWidgetSimpleUIManifest) "Yes(Default)" else "No"
                                    )
                                }
                            }
                            Text(
                                text = isWidgetUIManifestString,
                                modifier = Modifier.padding(all = 8.dp),
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Normal,
                                fontFamily = FontFamily.Default,
                                color = MaterialTheme.colorScheme.onBackground,
                                letterSpacing = 0.2.sp
                            )
                        }
                    }
                    Column (modifier = Modifier.weight(1f).padding(top = 4.dp)){
                        Switch(
                            modifier = Modifier,
                            checked = isWidgetSimpleUIManifest,
                            onCheckedChange = {
                                scope.launch {
                                    sharedDataStore.inverseIsWidgetSimpleUIManifest(context)
                                }
                            }
                        )
                    }
                }
            }
            item {
                HorizontalDivider(modifier = Modifier.padding(all = 4.dp).padding(top = 16.dp, bottom = 16.dp))
                Row(modifier = Modifier.padding(all = 8.dp), verticalAlignment = Alignment.Bottom) {
                    Column {
                        SimpleText("Information About This Widget ")
                        IndexedNoteText(1, "This widget is for manifesting battery charging information in real time. Two buttons for refreshing battery data instantly and toggling alarm, also by tapping widget can access main app.")
                        IndexedNoteText(2, "Updated data can be emitted by alarm interval when screen turned on, also when user turns on the screen (while alarm is running).")
                        IndexedNoteText(3, "To save battery, sometimes the alarm may be shut down automatically by operating system. You only need to reset the alarm by tapping cancel and also scheduled button, then the alarm will be activated.")
                    }
                }
            }
        }
    }
    /**
     * About This App Screen content.
     */
    @Composable
    fun AboutThisAppScreen(innerPadding: PaddingValues) {
        val url = "https://github.com/steven96034/BatteryWidget"
        val uriHandler = LocalUriHandler.current

        LazyColumn (modifier = Modifier.padding(6.dp), contentPadding = innerPadding) {
            item {
                SimpleText("This is the my FIRST Android app work!")
                SimpleText("I had it all arranged to design a battery widget that can update battery information in real time.")
                SimpleText("However, I have done all this app by the way and tried to design this more complete, also discover some cool new things in glance, compose UI, scaffold, navigation, DataStore, BatteryManager, and more.")
                SimpleText("I spend plenty of time but got lots of fun in this project! Hope you enjoy it!")
                SimpleText("For more information on my Github: ")
                Text(text = url, modifier = Modifier.padding(horizontal = 8.dp).clickable { uriHandler.openUri(url) }, fontSize = 16.sp, fontWeight = FontWeight.Normal, fontFamily = FontFamily.Default, color = MaterialTheme.colorScheme.secondary, letterSpacing = 0.2.sp, textDecoration = TextDecoration.Underline)
            }
        }
    }


    /**
     * Below for Preview
     */
    @Preview (showBackground = true)
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

    @Preview(showBackground = true)
    @Composable
    fun WidgetSettingsScreenPreview() {
        WidgetSettingsScreen(innerPadding = PaddingValues(0.dp))
    }

    @Preview(showBackground = true)
    @Composable
    fun AboutThisAppScreenPreview() {
        AboutThisAppScreen(innerPadding = PaddingValues(0.dp))
    }
}
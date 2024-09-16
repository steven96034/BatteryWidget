package com.example.batterywidget

import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.example.batterywidget.ui.theme.BatteryWidgetTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BatteryWidgetTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Greeting()
                }
            }
            CheckNotificationPermission()
        }

    }

    // Check if notification permission is granted when the app is launched.
    @Composable
    private fun CheckNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.POST_NOTIFICATIONS
                ) !=
                android.content.pm.PackageManager.PERMISSION_GRANTED
            ) {
                ShowNotificationPermissionDialog()
            }
        }
    }


    // Show dialog to request notification permission.
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    private fun ShowNotificationPermissionDialog() {
        val shouldShowDialog = remember { mutableStateOf(true) }
        if (shouldShowDialog.value) {
            BasicAlertDialog(onDismissRequest = {
            }) {
                Surface(
                    modifier = Modifier
                        .wrapContentWidth()
                        .wrapContentHeight(),
                    shape = MaterialTheme.shapes.large,
                    tonalElevation = AlertDialogDefaults.TonalElevation
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row {
                            Column(modifier = Modifier.align(Alignment.CenterVertically)) {
                                Image(
                                    painter = painterResource(id = android.R.drawable.ic_dialog_info),
                                    contentDescription = "Notification Permission Sign",
                                    colorFilter = ColorFilter.tint(Color.Black)
                                )
                            }
                            Spacer(
                                modifier = Modifier.width(16.dp)
                            )
                            Text(
                                text = "Notification Permission Required",
                                style = MaterialTheme.typography.headlineSmall,
                                textAlign = TextAlign.Center
                            )
                        }
                        Spacer(
                            modifier = Modifier.height(4.dp)
                        )
                        Text(
                            text = "To provide toast messages from widget, could you permit to receive notifications?",
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Spacer(
                            modifier = Modifier.height(24.dp)
                        )
                        Row(modifier = Modifier.align(Alignment.CenterHorizontally)) {
                            TextButton(
                                onClick = {
                                    requestNotificationPermission()
                                    shouldShowDialog.value = false
                                }
                            ) { Text("Confirm") }
                            Spacer(
                                modifier = Modifier.width(60.dp)
                            )
                            TextButton(
                                onClick = {
                                    shouldShowDialog.value = false
                                    // Do not manifest anything.
                                    // Or use some snackbar to manifest some messages.
                                }
                            ) {
                                Text("Dismiss")
                            }
                        }
                    }
                }
            }
        }
    }

    // Request notification permission.
    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    // Manifest notification permission if granted.
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            Toast.makeText(this, "Notification permission has been granted.", Toast.LENGTH_SHORT)
                .show()
        } else {
            // Do not manifest anything.
            // Or use some snackbar to manifest some messages.
        }
    }
}

// View of application
@Composable
fun Greeting() {
    Row(modifier = Modifier.padding(16.dp)) {
        Column(modifier = Modifier.align(alignment = Alignment.CenterVertically).padding(10.dp)) {
            Text(
                text = "Haven't implement settings here.",
                modifier = Modifier.align(alignment = Alignment.CenterHorizontally),
                style = MaterialTheme.typography.headlineSmall
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Back to homepage and enjoy the widget!",
                modifier = Modifier.align(alignment = Alignment.CenterHorizontally),
                style = MaterialTheme.typography.bodyLarge,
                color = Color.LightGray
            )
        }
    }
}
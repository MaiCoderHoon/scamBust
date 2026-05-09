package com.example.scambust

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import com.example.scambust.ui.theme.ScamBustTheme

class MainActivity : ComponentActivity() {

    private val viewModel: ScamBustViewModel by viewModels()

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        // Handle result of SMS permission requests if needed
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        requestPermissionsIfNeeded()

        handleIntent(intent)

        setContent {
            val status by viewModel.currentStatus.collectAsState()
            val sender by viewModel.currentSender.collectAsState()
            val message by viewModel.currentMessage.collectAsState()
            val showOverlayRationale by viewModel.showOverlayRationale.collectAsState()

            ScamBustTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    ScamBustUI(
                        modifier = Modifier.padding(innerPadding),
                        status = status,
                        sender = sender,
                        message = message,
                        onDismiss = {
                            viewModel.dismissScam()
                        }
                    )
                    
                    if (showOverlayRationale) {
                        AlertDialog(
                            onDismissRequest = { viewModel.setShowOverlayRationale(false) },
                            title = { Text("Permission Required") },
                            text = { Text("ScamBust needs the 'Display over other apps' permission to show high-contrast scam alerts instantly when a suspicious SMS arrives.") },
                            confirmButton = {
                                TextButton(onClick = {
                                    viewModel.setShowOverlayRationale(false)
                                    val intent = Intent(
                                        Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                                        Uri.parse("package:$packageName")
                                    )
                                    startActivity(intent)
                                }) {
                                    Text("Go to Settings")
                                }
                            },
                            dismissButton = {
                                TextButton(onClick = { viewModel.setShowOverlayRationale(false) }) {
                                    Text("Cancel")
                                }
                            }
                        )
                    }
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        handleIntent(intent)
    }

    private fun handleIntent(intent: Intent?) {
        intent?.let {
            val sender = it.getStringExtra("SCAM_SENDER")
            val message = it.getStringExtra("SCAM_BODY")
            
            if (sender != null && message != null) {
                viewModel.handleScamIntent(sender, message)
                
                // Prevent stale intents from triggering on rotation
                it.removeExtra("SCAM_SENDER")
                it.removeExtra("SCAM_BODY")
            }
        }
    }

    private fun requestPermissionsIfNeeded() {
        val permissions = mutableListOf(
            Manifest.permission.RECEIVE_SMS,
            Manifest.permission.READ_SMS
        )

        val permissionsToRequest = permissions.filter {
            ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
        }

        if (permissionsToRequest.isNotEmpty()) {
            requestPermissionLauncher.launch(permissionsToRequest.toTypedArray())
        }

        if (!Settings.canDrawOverlays(this)) {
            viewModel.setShowOverlayRationale(true)
        }
    }
}

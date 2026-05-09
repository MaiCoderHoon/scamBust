package com.example.scambust

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.scambust.ui.theme.ScamBustTheme

class MainActivity : ComponentActivity() {

    private var currentStatus by mutableStateOf(SafetyStatus.SAFE)
    private var currentSender by mutableStateOf<String?>(null)
    private var currentMessage by mutableStateOf<String?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        requestPermissionsIfNeeded()

        handleIntent(intent)

        setContent {
            ScamBustTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    androidx.compose.foundation.layout.Box(
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        ScamBustUI(
                            status = currentStatus,
                            sender = currentSender,
                            message = currentMessage,
                            onDismiss = {
                                currentStatus = SafetyStatus.SAFE
                                currentSender = null
                                currentMessage = null
                            }
                        )
                    }
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleIntent(intent)
    }

    private fun handleIntent(intent: Intent?) {
        intent?.let {
            val sender = it.getStringExtra("SCAM_SENDER")
            val message = it.getStringExtra("SCAM_BODY")
            if (sender != null && message != null) {
                currentStatus = SafetyStatus.SCAM
                currentSender = sender
                currentMessage = message
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
            ActivityCompat.requestPermissions(this, permissionsToRequest.toTypedArray(), 100)
        }

        // Request SYSTEM_ALERT_WINDOW permission
        if (!Settings.canDrawOverlays(this)) {
            val intent = Intent(
                Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:$packageName")
            )
            startActivity(intent)
        }
    }
}
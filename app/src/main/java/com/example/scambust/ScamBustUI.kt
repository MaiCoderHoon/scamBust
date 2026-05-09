package com.example.scambust

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

enum class SafetyStatus {
    SAFE, SUSPICIOUS, SCAM
}

@Composable
fun ScamBustUI(
    status: SafetyStatus,
    sender: String? = null,
    message: String? = null,
    onDismiss: () -> Unit = {}
) {
    val backgroundColor = when (status) {
        SafetyStatus.SAFE -> Color(0xFF2E7D32) // High contrast Green
        SafetyStatus.SUSPICIOUS -> Color(0xFFF9A825) // High contrast Yellow/Orange
        SafetyStatus.SCAM -> Color(0xFFD32F2F) // High contrast Red
    }

    val contentColor = when (status) {
        SafetyStatus.SUSPICIOUS -> Color.Black
        else -> Color.White
    }
    
    val statusText = when (status) {
        SafetyStatus.SAFE -> "NO THREAT DETECTED"
        SafetyStatus.SUSPICIOUS -> "SUSPICIOUS ACTIVITY"
        SafetyStatus.SCAM -> "SCAM ALERT"
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                imageVector = if (status == SafetyStatus.SAFE) Icons.Default.CheckCircle else Icons.Default.Warning,
                contentDescription = "Status Icon",
                tint = contentColor,
                modifier = Modifier.height(120.dp).fillMaxWidth()
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            Text(
                text = statusText,
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = contentColor,
                textAlign = TextAlign.Center,
                fontSize = 36.sp,
                lineHeight = 44.sp
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            if (sender != null && message != null) {
                Text(
                    text = "From: $sender",
                    style = MaterialTheme.typography.titleLarge,
                    color = contentColor,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center,
                    fontSize = 24.sp
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    text = message,
                    style = MaterialTheme.typography.bodyLarge,
                    color = contentColor,
                    textAlign = TextAlign.Center,
                    fontSize = 20.sp,
                    lineHeight = 28.sp
                )
                
                Spacer(modifier = Modifier.height(48.dp))
                
                Button(
                    onClick = onDismiss,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = contentColor,
                        contentColor = backgroundColor
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(72.dp) // Large touch target > 60dp
                ) {
                    Text(
                        text = "DISMISS",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            } else {
                Text(
                    text = "Waiting for incoming messages...",
                    style = MaterialTheme.typography.titleLarge,
                    color = contentColor,
                    textAlign = TextAlign.Center,
                    fontSize = 24.sp
                )
            }
        }
    }
}

package com.example.scambust

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.PixelFormat
import android.os.Build
import android.provider.Telephony
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.TextView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SmsReceiver : BroadcastReceiver() {
    private val scope = CoroutineScope(Dispatchers.IO)

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Telephony.Sms.Intents.SMS_RECEIVED_ACTION) {
            val messages = Telephony.Sms.Intents.getMessagesFromIntent(intent)
            for (sms in messages) {
                val sender = sms.displayOriginatingAddress ?: "Unknown"
                val body = sms.displayMessageBody ?: ""
                
                Log.d("SmsReceiver", "Received SMS from: $sender")
                
                // Analyze SMS via Backend
                scope.launch {
                    try {
                        val request = SmsRequest(sender, body)
                        val response = RetrofitClient.instance.analyzeSms(request)
                        
                        Log.d("SmsReceiver", "Verdict: ${response.verdict}, Confidence: ${response.confidence}")
                        
                        if (response.verdict == "SCAM") {
                            showScamOverlay(context, sender, body)
                        }
                    } catch (e: Exception) {
                        Log.e("SmsReceiver", "Error analyzing SMS: ${e.message}")
                    }
                }
            }
        }
    }

    private fun showScamOverlay(context: Context, sender: String, message: String) {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
                
                val layoutParams = WindowManager.LayoutParams(
                    WindowManager.LayoutParams.MATCH_PARENT,
                    WindowManager.LayoutParams.MATCH_PARENT,
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
                    } else {
                        @Suppress("DEPRECATION")
                        WindowManager.LayoutParams.TYPE_PHONE
                    },
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or 
                    WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
                    PixelFormat.TRANSLUCENT
                )
                
                layoutParams.gravity = Gravity.CENTER
                
                // Create overlay view manually (or inflate from XML if we had one)
                // For simplicity, building it programmatically here
                val overlayView = android.widget.LinearLayout(context).apply {
                    orientation = android.widget.LinearLayout.VERTICAL
                    gravity = Gravity.CENTER
                    setBackgroundColor(Color.parseColor("#D32F2F")) // High-contrast Red
                    setPadding(64, 64, 64, 64)
                }
                
                val titleView = TextView(context).apply {
                    text = "⚠️ SCAM ALERT ⚠️"
                    textSize = 32f
                    setTextColor(Color.WHITE)
                    setTypeface(null, android.graphics.Typeface.BOLD)
                    gravity = Gravity.CENTER
                    setPadding(0, 0, 0, 32)
                }
                
                val senderView = TextView(context).apply {
                    text = "From: $sender"
                    textSize = 20f
                    setTextColor(Color.WHITE)
                    setPadding(0, 0, 0, 16)
                }
                
                val bodyView = TextView(context).apply {
                    text = message
                    textSize = 18f
                    setTextColor(Color.WHITE)
                    setPadding(0, 0, 0, 64)
                }
                
                val dismissButton = Button(context).apply {
                    text = "DISMISS"
                    textSize = 24f
                    setBackgroundColor(Color.WHITE)
                    setTextColor(Color.parseColor("#D32F2F"))
                    setPadding(32, 32, 32, 32)
                    setOnClickListener {
                        windowManager.removeView(overlayView)
                    }
                }
                
                overlayView.addView(titleView)
                overlayView.addView(senderView)
                overlayView.addView(bodyView)
                overlayView.addView(dismissButton)
                
                windowManager.addView(overlayView, layoutParams)
            } catch (e: Exception) {
                Log.e("SmsReceiver", "Failed to show overlay. Did you grant SYSTEM_ALERT_WINDOW permission?", e)
                // Fallback to launching MainActivity if overlay fails
                val intent = Intent(context, MainActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                    putExtra("SCAM_SENDER", sender)
                    putExtra("SCAM_BODY", message)
                }
                context.startActivity(intent)
            }
        }
    }
}

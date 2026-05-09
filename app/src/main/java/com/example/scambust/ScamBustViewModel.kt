package com.example.scambust

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class ScamBustViewModel : ViewModel() {

    private val _currentStatus = MutableStateFlow(SafetyStatus.SAFE)
    val currentStatus: StateFlow<SafetyStatus> = _currentStatus.asStateFlow()

    private val _currentSender = MutableStateFlow<String?>(null)
    val currentSender: StateFlow<String?> = _currentSender.asStateFlow()

    private val _currentMessage = MutableStateFlow<String?>(null)
    val currentMessage: StateFlow<String?> = _currentMessage.asStateFlow()

    private val _showOverlayRationale = MutableStateFlow(false)
    val showOverlayRationale: StateFlow<Boolean> = _showOverlayRationale.asStateFlow()

    fun handleScamIntent(sender: String, message: String) {
        _currentStatus.value = SafetyStatus.SCAM
        _currentSender.value = sender
        _currentMessage.value = message
    }

    fun dismissScam() {
        _currentStatus.value = SafetyStatus.SAFE
        _currentSender.value = null
        _currentMessage.value = null
    }

    fun setShowOverlayRationale(show: Boolean) {
        _showOverlayRationale.value = show
    }
}

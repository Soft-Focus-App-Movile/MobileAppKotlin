package com.softfocus.features.home.presentation.psychologist

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.softfocus.features.psychologist.domain.models.InvitationCode
import com.softfocus.features.psychologist.domain.repositories.PsychologistRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PsychologistHomeViewModel(
    private val psychologistRepository: PsychologistRepository,
    private val context: Context
) : ViewModel() {

    private val _invitationCode = MutableStateFlow<InvitationCode?>(null)
    val invitationCode: StateFlow<InvitationCode?> = _invitationCode.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    init {
        loadInvitationCode()
    }

    private fun loadInvitationCode() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            val result = psychologistRepository.getInvitationCode()
            result.onSuccess { code ->
                _invitationCode.value = code
                _isLoading.value = false
            }.onFailure { exception ->
                _errorMessage.value = exception.message ?: "Error al cargar el código"
                _isLoading.value = false
            }
        }
    }

    fun copyCodeToClipboard() {
        val code = _invitationCode.value?.code ?: return
        val clipboardManager = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("Código de invitación", code)
        clipboardManager.setPrimaryClip(clip)
    }

    fun shareCode() {
        val code = _invitationCode.value?.code ?: return
        val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, "Mi código de invitación de Soft Focus: $code")
            type = "text/plain"
        }
        val shareIntent = Intent.createChooser(sendIntent, "Compartir código")
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(shareIntent)
    }
}

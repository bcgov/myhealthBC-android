package ca.bc.gov.bchealth.ui.healthpass

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ca.bc.gov.repository.QrCodeGeneratorRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * @author Pinakin Kansara
 */
@HiltViewModel
class QrCodeGeneratorViewModel @Inject constructor(
    private val qrCodeGeneratorRepository: QrCodeGeneratorRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(QrCodeUiState())
    val uiState: StateFlow<QrCodeUiState> = _uiState.asStateFlow()

    fun generateQrCode(shcUri: String) = viewModelScope.launch {
        _uiState.update { it.copy(onLoading = true) }
        val bitmap = qrCodeGeneratorRepository.generateQRCode(shcUri)
        _uiState.update { it.copy(onLoading = false, qrCodeImage = bitmap) }
    }
}

data class QrCodeUiState(
    val onLoading: Boolean = false,
    val qrCodeImage: Bitmap? = null
)
package ca.bc.gov.bchealth.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ca.bc.gov.repository.PdfDecoderRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

/**
 * @author Pinakin Kansara
 */
@HiltViewModel
class PdfDecoderViewModel @Inject constructor(
    private val pdfDecoderRepository: PdfDecoderRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(PdfDecoderUiState())
    val uiState: StateFlow<PdfDecoderUiState> = _uiState.asStateFlow()

    fun base64ToPDFFile(base64: String) = viewModelScope.launch {
        try {
            val file = pdfDecoderRepository.base64ToPDF(base64)
            _uiState.update {
                it.copy(pdf = Pair(base64, file))
            }
        } catch (e: Exception) {
            _uiState.update {
                it.copy(pdf = Pair(base64, null))
            }
        }
    }

    fun resetUiState() {
        _uiState.update { federalTravelPassDecoderUiState ->
            federalTravelPassDecoderUiState.copy(pdf = null)
        }
    }
}

data class PdfDecoderUiState(
    val pdf: Pair<String, File?>? = null
)

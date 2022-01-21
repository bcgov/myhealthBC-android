package ca.bc.gov.bchealth.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ca.bc.gov.repository.FederalTravelPassDecoderRepository
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
class FederalTravelPassDecoderVideModel @Inject constructor(
    private val federalTravelPassDecoderRepository: FederalTravelPassDecoderRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(FederalTravelPassDecoderUiState())
    val uiState: StateFlow<FederalTravelPassDecoderUiState> = _uiState.asStateFlow()

    fun base64ToPDFFile(base64: String) = viewModelScope.launch {
        try {
            val file = federalTravelPassDecoderRepository.base64ToPDF(base64)
            _uiState.update { federalTravelPassDecoderUiState ->
                federalTravelPassDecoderUiState.copy(travelPass = Pair(base64, file))
            }
        } catch (e: Exception) {
            e.printStackTrace()
            _uiState.update { federalTravelPassDecoderUiState ->
                federalTravelPassDecoderUiState.copy(travelPass = Pair(base64, null))
            }
        }
    }

    fun federalTravelPassShown() {
        _uiState.update { federalTravelPassDecoderUiState ->
            federalTravelPassDecoderUiState.copy(travelPass = null)
        }
    }
}

data class FederalTravelPassDecoderUiState(
    val travelPass: Pair<String, File?>? = null
)
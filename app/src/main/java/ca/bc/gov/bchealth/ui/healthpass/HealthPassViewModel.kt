package ca.bc.gov.bchealth.ui.healthpass

import android.content.Context
import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ca.bc.gov.bchealth.R
import ca.bc.gov.bchealth.model.mapper.toUiModel
import ca.bc.gov.common.model.ImmunizationStatus
import ca.bc.gov.common.utils.toDateTimeString
import ca.bc.gov.repository.PatientWithVaccineRecordRepository
import ca.bc.gov.repository.vaccine.VaccineRecordRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * @author Pinakin Kansara
 */
@HiltViewModel
class HealthPassViewModel @Inject constructor(
    private val repository: PatientWithVaccineRecordRepository,
    private val vaccineRecordRepository: VaccineRecordRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HealthPassUiState())
    val uiState: StateFlow<HealthPassUiState> = _uiState.asStateFlow()

    val healthPasses = repository.patientsVaccineRecord.map { records ->
        records.map { record ->
            record.toUiModel()
        }
    }

    fun loadHealthPasses() = viewModelScope.launch {
        _uiState.update { healthPassUiState -> healthPassUiState.copy(isLoading = true) }
        //TODO: check for onBoarding require or not
        repository.patientsVaccineRecord.collect { patientVaccineRecords ->
            val healthPasses = patientVaccineRecords.map { record ->
                record.toUiModel()
            }
            _uiState.update { healthPassUiState ->
                healthPassUiState.copy(
                    isLoading = false,
                    healthPasses = healthPasses
                )
            }
        }
    }

    fun deleteHealthPass(vaccineRecordId: Long) = viewModelScope.launch {
        vaccineRecordRepository.delete(vaccineRecordId = vaccineRecordId)
    }

    fun updateHealthPassOrder(healthPasses: List<HealthPass>)
        = viewModelScope.launch {
        repository.updatePatientOrder(healthPasses.mapIndexed { index, healthPass ->
            healthPass.patientId to index.toLong()
        })
    }
}

data class HealthPassUiState(
    val isLoading: Boolean = false,
    val isOnBoardingShown: Boolean = false,
    val healthPasses: List<HealthPass> = emptyList()
)

data class HealthPass(
    val patientId: Long,
    val vaccineRecordId: Long,
    val isExpanded: Boolean = true,
    val name: String,
    val qrIssuedDate: String?,
    val shcUri: String,
    val qrCode: Bitmap?,
    val federalTravelPassState: FederalTravelPassState,
    val state: PassState
)

data class PassState(
    val color: Int,
    val status: Int,
    val icon: Int
)

data class FederalTravelPassState(
    val title: Int,
    val icon: Int,
    val pdf: String?
)
package ca.bc.gov.bchealth.ui.healthpass

import android.content.Context
import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ca.bc.gov.bchealth.R
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
            HealthPass(
                record.patient.id,
                record.patient.firstName,
                record.patient.lastName,
                record.vaccineRecord.qrIssueDate.toDateTimeString(),
                record.vaccineRecord.shcUri!!,
                record.vaccineRecord.qrCodeImage,
                record.vaccineRecord.status,
                record.vaccineRecord.id,
                record.vaccineRecord.federalPass
            )
        }
    }

    fun loadHealthPasses() = viewModelScope.launch {
        _uiState.update { healthPassUiState -> healthPassUiState.copy(isLoading = true) }
        //TODO: check for onBoarding require or not
        repository.patientsVaccineRecord.collect { patientVaccineRecords ->
            val healthPasses = patientVaccineRecords.map { record ->
                HealthPass(
                    record.patient.id,
                    record.patient.firstName,
                    record.patient.lastName,
                    record.vaccineRecord.qrIssueDate.toDateTimeString(),
                    record.vaccineRecord.shcUri!!,
                    record.vaccineRecord.qrCodeImage,
                    record.vaccineRecord.status,
                    record.vaccineRecord.id,
                    record.vaccineRecord.federalPass
                )
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
}

data class HealthPassUiState(
    val isLoading: Boolean = false,
    val isOnBoardingShown: Boolean = false,
    val healthPasses: List<HealthPass> = emptyList()
)

data class HealthPass(
    val patientId: Long,
    val firstName: String,
    val lastName: String,
    val qrIssuedDate: String?,
    val shcUri: String,
    val qrCode: Bitmap?,
    val status: ImmunizationStatus?,
    val vaccineRecordId: Long,
    val federalPass: String?,
)

fun ImmunizationStatus.getHealthPassStatus(context: Context): PassState =
    when (this) {
        ImmunizationStatus.FULLY_IMMUNIZED -> {
            PassState(
                color = context.getColor(R.color.status_green),
                context.resources
                    .getString(R.string.vaccinated),
                R.drawable.ic_check_mark
            )
        }
        ImmunizationStatus.PARTIALLY_IMMUNIZED -> {
            PassState(
                color = context.getColor(R.color.blue),
                context.resources
                    .getString(R.string.partially_vaccinated),
                0
            )
        }

        ImmunizationStatus.INVALID -> {
            PassState(
                color = context.getColor(R.color.grey),
                context.resources
                    .getString(R.string.no_record),
                0
            )
        }
    }

data class PassState(
    val color: Int,
    val status: String,
    val icon: Int
)

fun HealthPass.displayName() = "$firstName $lastName"
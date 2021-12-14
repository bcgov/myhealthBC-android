package ca.bc.gov.bchealth.ui.healthpass

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.room.PrimaryKey
import ca.bc.gov.common.model.ImmunizationStatus
import ca.bc.gov.common.utils.toDateTimeString
import ca.bc.gov.repository.PatientWithVaccineRecordRepository
import ca.bc.gov.repository.QrCodeGeneratorRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * @author Pinakin Kansara
 */
@HiltViewModel
class HealthPassViewModel @Inject constructor(
    private val repository: PatientWithVaccineRecordRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HealthPassUiState())
    val uiState: StateFlow<HealthPassUiState> = _uiState.asStateFlow()

    val healthPasses = repository.patientsVaccineRecord.map { records ->
        records.map { record ->
            HealthPass(
                record.patient.firstName,
                record.patient.lastName,
                record.vaccineRecord.qrIssueDate.toDateTimeString(),
                record.vaccineRecord.shcUri!!,
                record.vaccineRecord.qrCodeImage,
                record.vaccineRecord.status
            )
        }
    }
}

data class HealthPassUiState(
    val onBoardingRequired: Boolean = false
)

data class HealthPass(
    val firstName: String,
    val lastName: String,
    val qrIssuedDate: String?,
    val shcUri: String,
    val qrCode: Bitmap?,
    val status: ImmunizationStatus?
)

fun HealthPass.displayName() = "$firstName $lastName"
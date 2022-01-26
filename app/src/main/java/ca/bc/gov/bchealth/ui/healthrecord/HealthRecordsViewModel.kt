package ca.bc.gov.bchealth.ui.healthrecord

import androidx.lifecycle.ViewModel
import ca.bc.gov.bchealth.model.mapper.toUiModel
import ca.bc.gov.repository.PatientHealthRecordsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * @author Pinakin Kansara
 */
@HiltViewModel
class HealthRecordsViewModel @Inject constructor(
    private val repository: PatientHealthRecordsRepository
) : ViewModel() {

    val patientHealthRecords = repository.patientHealthRecords.map { records ->
        records.map { record ->
            record.toUiModel()
        }
    }
}

data class PatientHealthRecord(
    val patientId: Long,
    val name: String,
    val totalRecord: Int
)

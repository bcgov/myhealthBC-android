package ca.bc.gov.bchealth.ui.healthrecord

import androidx.lifecycle.ViewModel
import ca.bc.gov.bchealth.model.mapper.toUiModel
import ca.bc.gov.repository.patient.PatientRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@HiltViewModel
class HealthRecordPlaceholderViewModel @Inject constructor(
    repository: PatientRepository
) : ViewModel() {

    val patientHealthRecords = repository.patientHealthRecords.map { records ->
        records.map { record ->
            record.toUiModel()
        }
    }
}

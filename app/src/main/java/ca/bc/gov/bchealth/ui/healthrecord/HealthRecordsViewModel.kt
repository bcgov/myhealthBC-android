package ca.bc.gov.bchealth.ui.healthrecord

import androidx.lifecycle.ViewModel
import ca.bc.gov.repository.PatientHealthRecordsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * @author Pinakin Kansara
 */
@HiltViewModel
class HealthRecordsViewModel @Inject constructor(
    private val repository: PatientHealthRecordsRepository
) : ViewModel() {

    val patientHealthRecords = repository.patientHealthRecords
}


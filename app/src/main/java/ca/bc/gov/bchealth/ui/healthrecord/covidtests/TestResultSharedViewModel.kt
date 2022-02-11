package ca.bc.gov.bchealth.ui.healthrecord.covidtests

import androidx.lifecycle.ViewModel
import ca.bc.gov.common.model.patient.PatientDto
import ca.bc.gov.common.model.test.TestRecordDto

/*
* Created by amit_metri on 11,February,2022
*/
class TestResultSharedViewModel : ViewModel() {
    var testRecordDto: TestRecordDto? = null
    var patientDto: PatientDto? = null
}

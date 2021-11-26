package ca.bc.gov.bchealth.ui.healthrecords

import androidx.lifecycle.ViewModel
import ca.bc.gov.bchealth.model.HealthCardDto
import ca.bc.gov.bchealth.model.healthrecords.HealthRecord
import ca.bc.gov.bchealth.repository.HealthRecordsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/*
* Created by amit_metri on 25,November,2021
*/
@HiltViewModel
class IndividualHealthRecordViewModel @Inject constructor(
    val healthRecordsRepository: HealthRecordsRepository
) : ViewModel() {

    fun prepareVaccineDataList(healthRecordDto: HealthCardDto): HealthRecord {
        return healthRecordsRepository.prepareHealthRecords(healthRecordDto)
    }
}
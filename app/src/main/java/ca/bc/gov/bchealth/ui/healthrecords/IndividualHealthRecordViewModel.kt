package ca.bc.gov.bchealth.ui.healthrecords

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ca.bc.gov.bchealth.datasource.LocalDataSource
import ca.bc.gov.bchealth.model.healthrecords.HealthRecord
import ca.bc.gov.bchealth.model.healthrecords.IndividualRecord
import ca.bc.gov.bchealth.repository.HealthRecordsRepository
import ca.bc.gov.bchealth.utils.getDateForIndividualCovidTestResult
import ca.bc.gov.bchealth.utils.getDateForIndividualVaccineRecord
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/*
* Created by amit_metri on 25,November,2021
*/
@HiltViewModel
class IndividualHealthRecordViewModel @Inject constructor(
    val healthRecordsRepository: HealthRecordsRepository,
    val dataSource: LocalDataSource
) : ViewModel() {

    companion object {
        const val bulletPoint = " \u2022 "
    }

    val healthRecords = healthRecordsRepository.healthRecords.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = null
    )

    fun prepareIndividualRecords(healthRecord: HealthRecord): MutableList<IndividualRecord> {

        val individualRecords: MutableList<IndividualRecord> = mutableListOf()

        if (healthRecord.vaccineDataList.isNotEmpty()) {

            individualRecords.add(
                IndividualRecord(
                    "Covid-19 vaccination",
                    healthRecord.vaccineDataList.last()?.occurrenceDate
                        ?.getDateForIndividualVaccineRecord(),
                    IndividualHealthRecordAdapter.HealthRecordType.VACCINE_RECORD,
                    null
                )
            )
        }

        healthRecord.covidTestResultList.forEach {
            individualRecords.add(
                IndividualRecord(
                    "Covid-19 Test Result",
                    it.testStatus
                        .plus(bulletPoint)
                        .plus(it.resultDateTime.getDateForIndividualCovidTestResult()),
                    IndividualHealthRecordAdapter.HealthRecordType.COVID_TEST_RECORD,
                    it.reportId
                )
            )
        }

        return individualRecords
    }

    fun deleteCovidTestResult(reportId: String) = viewModelScope.launch {
        dataSource.deleteCovidTestResult(reportId)
    }

    fun deleteVaccineRecord(healthPassId: Int) = viewModelScope.launch {
        dataSource.deleteVaccineData(healthPassId)
    }
}

package ca.bc.gov.bchealth.ui.healthrecords

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ca.bc.gov.bchealth.datasource.LocalDataSource
import ca.bc.gov.bchealth.model.ImmunizationStatus
import ca.bc.gov.bchealth.model.healthrecords.HealthRecord
import ca.bc.gov.bchealth.model.healthrecords.IndividualRecord
import ca.bc.gov.bchealth.repository.HealthRecordsRepository
import ca.bc.gov.bchealth.utils.getDateForIndividualHealthRecord
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

    val healthRecords = healthRecordsRepository.healthRecords.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = null
    )

    fun prepareIndividualRecords(healthRecord: HealthRecord): MutableList<IndividualRecord> {

        val individualRecords: MutableList<IndividualRecord> = mutableListOf()

        if (healthRecord.vaccineDataList.isNotEmpty()) {

            val productInfo = healthRecord.vaccineDataList.last()?.product

            individualRecords.add(
                IndividualRecord(
                    productInfo.toString(),
                    getSubTitle(healthRecord),
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
                        .plus(" \u2022 ")
                        .plus(it.resultDateTime.getDateForIndividualHealthRecord()),
                    IndividualHealthRecordAdapter.HealthRecordType.COVID_TEST_RECORD,
                    it.reportId
                )
            )
        }

        return individualRecords
    }

    private fun getSubTitle(healthRecord: HealthRecord): String {
        return when (healthRecord.immunizationStatus) {
            ImmunizationStatus.FULLY_IMMUNIZED -> {
                return "Vaccinated"
                    .plus(" \u2022 ")
                    .plus(
                        healthRecord.vaccineDataList.last()?.occurrenceDate
                            ?.getDateForIndividualHealthRecord()
                    )
            }
            ImmunizationStatus.PARTIALLY_IMMUNIZED -> {
                return "Partially Vaccinated"
                    .plus(" \u2022 ")
                    .plus(
                        healthRecord.vaccineDataList.last()?.occurrenceDate
                            ?.getDateForIndividualHealthRecord()
                    )
            }
            ImmunizationStatus.INVALID_QR_CODE -> {
                return "No Record"
            }
            else -> "No Record"
        }
    }

    fun deleteCovidTestResult(reportId: String) = viewModelScope.launch {
        dataSource.deleteCovidTestResult(reportId)
    }
}

package ca.bc.gov.bchealth.ui.healthrecords

import androidx.lifecycle.ViewModel
import ca.bc.gov.bchealth.model.ImmunizationStatus
import ca.bc.gov.bchealth.model.healthrecords.HealthRecord
import ca.bc.gov.bchealth.model.healthrecords.IndividualRecord
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
                    it.testStatus,
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
                    .plus(" ")
                    .plus(healthRecord.vaccineDataList.last()?.occurrenceDate)
            }
            ImmunizationStatus.PARTIALLY_IMMUNIZED -> {
                return "Partially Vaccinated"
                    .plus(" ")
                    .plus(healthRecord.vaccineDataList.last()?.occurrenceDate)
            }
            ImmunizationStatus.INVALID_QR_CODE -> {
                return "No Record"
            }
            else -> "No Record"
        }
    }
}
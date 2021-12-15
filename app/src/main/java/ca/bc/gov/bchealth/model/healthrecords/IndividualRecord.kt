package ca.bc.gov.bchealth.model.healthrecords

import ca.bc.gov.bchealth.data.local.entity.CovidTestResult
import ca.bc.gov.bchealth.model.ImmunizationStatus
import ca.bc.gov.bchealth.repository.HealthRecordType

/*
* Created by amit_metri on 30,November,2021
*/
data class IndividualRecord(
    val title: String,
    val subtitle: String?,
    val name: String,
    val status: ImmunizationStatus?,
    val issueDate: String = "",
    val healthRecordType: HealthRecordType,
    val covidTestReportId: String?,
    val healthPassId: Int = 0,
    val vaccineDataList: List<VaccineData?>,
    var covidTestResultList: List<CovidTestResult>
)

fun IndividualRecord.toHealthRecord(): HealthRecord = HealthRecord(
    healthPassId,
    name,
    status,
    issueDate,
    vaccineDataList,
    covidTestResultList
)

package ca.bc.gov.bchealth.model.healthrecords

import ca.bc.gov.bchealth.ui.healthrecords.IndividualHealthRecordAdapter

/*
* Created by amit_metri on 30,November,2021
*/
data class IndividualRecord(
    val title: String,
    val subtitle: String,
    val healthRecordType: IndividualHealthRecordAdapter.HealthRecordType,
    val covidTestReportId: String?
)

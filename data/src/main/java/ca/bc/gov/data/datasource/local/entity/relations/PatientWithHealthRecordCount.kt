package ca.bc.gov.data.datasource.local.entity.relations

import androidx.room.Embedded
import ca.bc.gov.data.datasource.local.entity.PatientEntity

/**
 * @author Pinakin Kansara
 */
data class PatientWithHealthRecordCount(
    @Embedded
    val patientEntity: PatientEntity,
    val vaccineRecordCount: Int,
    val testRecordCount: Int,
    val labTestCount: Int,
    val covidTestCount: Int,
    val immunizationCount: Int,
    val medicationRecordCount: Int
)

package ca.bc.gov.data.local.entity.relations

import androidx.room.Embedded
import ca.bc.gov.data.local.entity.PatientEntity

/**
 * @author Pinakin Kansara
 */
data class PatientWithHealthRecordCount(
    @Embedded
    val patientEntity: PatientEntity,
    val vaccineRecordCount: Int,
    val testRecordCount: Int,
    val medicationRecordCount: Int
)

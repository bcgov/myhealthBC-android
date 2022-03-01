package ca.bc.gov.common.model.labtest

import java.time.Instant

/**
 * @author: Created by Rashmi Bambhania on 01,March,2022
 */
data class LabTestRecordDto(
    val patientId: Long = 0L,
    val testStatus: String = "Pending",
    val testDate: Instant = Instant.now()
)
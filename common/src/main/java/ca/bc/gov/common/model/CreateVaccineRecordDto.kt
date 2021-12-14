package ca.bc.gov.common.model

import java.time.Instant

/**
 * @author Pinakin Kansara
 */
data class CreateVaccineRecordDto(
    val id: Long = 0,
    val patientId: Long,
    val qrIssueDate: Instant,
    val status: ImmunizationStatus,
    val shcUri: String,
    val federalPass: String? = null,
    val dataSource: DataSource
)

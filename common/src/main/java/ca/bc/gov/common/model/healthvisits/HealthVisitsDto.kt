package ca.bc.gov.common.model.healthvisits

import ca.bc.gov.common.model.DataSource
import java.time.Instant

/**
 * @author: Created by Rashmi Bambhania on 20,June,2022
 */
data class HealthVisitsDto(
    val healthVisitId: Long = 0,
    var patientId: Long,
    val id: String? = null,
    val encounterDate: Instant?,
    val specialtyDescription: String? = null,
    val practitionerName: String? = null,
    val clinicDto: ClinicDto? = null,
    val dataSource: DataSource = DataSource.BCSC
)

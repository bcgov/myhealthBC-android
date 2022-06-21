package ca.bc.gov.common.model.healthvisits

/**
 * @author: Created by Rashmi Bambhania on 20,June,2022
 */
data class HealthVisitsDto(
    val id: String? = null,
    val encounterDate: String? = null,
    val specialtyDescription: String? = null,
    val practitionerName: String? = null,
    val clinic: ClinicDto
)
package ca.bc.gov.data.datasource.remote.model.base.healthvisits

/**
 * @author: Created by Rashmi Bambhania on 20,June,2022
 */
class HealthVisitsPayload(
    val id: String? = null,
    val encounterDate: String,
    val specialtyDescription: String? = null,
    val practitionerName: String? = null,
    val clinic: Clinic
)

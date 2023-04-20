package ca.bc.gov.data.datasource.remote.model.base

/**
 * @author Pinakin Kansara
 * [ApiWarning] is part of v2 api response of patient service @see [PatientResponse]
 * TODO: required clarity on what to do when we have this api warning in the response from the server
 */
data class ApiWarning(
    val code: String?,
    val message: String?
)

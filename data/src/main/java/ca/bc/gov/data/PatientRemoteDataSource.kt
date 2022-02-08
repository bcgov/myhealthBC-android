package ca.bc.gov.data

import ca.bc.gov.common.const.SERVER_ERROR
import ca.bc.gov.common.exceptions.MyHealthNetworkException
import ca.bc.gov.data.remote.PatientApi
import ca.bc.gov.data.remote.model.base.PatientPayload
import ca.bc.gov.data.remote.model.request.PatientRequest
import ca.bc.gov.data.remote.model.request.toMap
import ca.bc.gov.data.remote.model.response.PatientResponse
import ca.bc.gov.data.utils.safeCall
import javax.inject.Inject

class PatientRemoteDataSource @Inject constructor(private val patientApi: PatientApi) {

    suspend fun getPatient(token: String, hdid: String): PatientPayload {
        val response = safeCall { patientApi.getPatient(token = "Bearer $token",hdid = hdid) }
            ?: throw MyHealthNetworkException(SERVER_ERROR, "Invalid Response")


        if (response.error != null) {
            throw MyHealthNetworkException(SERVER_ERROR, response.error.message)
        }

        if (!isResponseValid(response)) {
            throw MyHealthNetworkException(SERVER_ERROR, "Invalid Response")
        }
        return response.payload!!
    }

    private fun isResponseValid(response: PatientResponse): Boolean {
        var isValid = false
        if (response.payload != null)
            with(response.payload) {
                isValid = when {
                    hdid.isNullOrBlank() ||
                        personalHealthNumber.isNullOrBlank() ||
                        firstName.isNullOrBlank() ||
                        lastName.isNullOrBlank() ||
                        birthDate.isNullOrBlank() ||
                        gender.isNullOrBlank() -> {
                        false
                    }
                    else -> {
                        true
                    }
                }
            }
        return isValid
    }
}
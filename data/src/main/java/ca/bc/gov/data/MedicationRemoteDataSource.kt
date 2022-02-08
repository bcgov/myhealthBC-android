package ca.bc.gov.data

import ca.bc.gov.common.const.SERVER_ERROR
import ca.bc.gov.common.exceptions.MyHealthNetworkException
import ca.bc.gov.data.remote.MedicationApi
import ca.bc.gov.data.utils.safeCall
import javax.inject.Inject

/*
* Created by amit_metri on 08,February,2022
*/
class MedicationRemoteDataSource @Inject constructor(
    private val medicationApi: MedicationApi
) {

    suspend fun getMedicationStatement(hdid: String, accessToken: String) {
        val response = safeCall { medicationApi.getMedicationStatement(hdid, accessToken) }
            ?: throw MyHealthNetworkException(SERVER_ERROR, "Invalid Response")

        if (response.error != null) {
            throw MyHealthNetworkException(SERVER_ERROR, response.error.message)
        }

        // TODO: 08/02/22 Response validations to be placed
    }
}

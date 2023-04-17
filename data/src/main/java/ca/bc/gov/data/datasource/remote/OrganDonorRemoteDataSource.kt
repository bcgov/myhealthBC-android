package ca.bc.gov.data.datasource.remote

import android.util.Base64
import ca.bc.gov.common.const.MESSAGE_INVALID_RESPONSE
import ca.bc.gov.common.const.SERVER_ERROR
import ca.bc.gov.common.exceptions.MyHealthException
import ca.bc.gov.common.model.services.OrganDonationDto
import ca.bc.gov.data.datasource.remote.api.HealthGatewayPrivateApi
import ca.bc.gov.data.datasource.remote.model.base.patientdata.PatientDataType
import ca.bc.gov.data.model.mapper.toDto
import ca.bc.gov.data.utils.safeCall
import javax.inject.Inject

/**
 * @author Pinakin Kansara
 */
class OrganDonorRemoteDataSource @Inject constructor(
    private val healthGatewayPrivateApi: HealthGatewayPrivateApi
) {

    /**
     * This API is used to get organ donor data based on type
     * @see [PatientDataType]
     * uses api = v2
     */
    suspend fun getOrganDonorData(hdid: String, token: String): OrganDonationDto {
        val response = safeCall {
            healthGatewayPrivateApi.getPatientData(
                hdid,
                token,
                listOf(PatientDataType.ORGAN_DONOR.value)
            )
        }
            ?: throw MyHealthException(SERVER_ERROR, MESSAGE_INVALID_RESPONSE)
        val result = response.items.firstOrNull() ?: throw MyHealthException(SERVER_ERROR, MESSAGE_INVALID_RESPONSE)
        return result.toDto()
    }

    suspend fun getPatientFile(hdid: String, token: String, fileId: String): String {
        val response = safeCall {
            healthGatewayPrivateApi.getPatientFile(hdid, token, fileId)
        } ?: throw MyHealthException(SERVER_ERROR, MESSAGE_INVALID_RESPONSE)

        val result = response.content.foldIndexed(ByteArray(response.content.size)) { i, a, v -> a.apply { set(i, v.toByte()) } }
        return Base64.encodeToString(result, Base64.DEFAULT)
    }
}

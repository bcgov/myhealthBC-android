package ca.bc.gov.data.datasource.remote

import android.util.Base64
import ca.bc.gov.common.const.MESSAGE_INVALID_RESPONSE
import ca.bc.gov.common.const.SERVER_ERROR
import ca.bc.gov.common.exceptions.MyHealthException
import ca.bc.gov.common.model.services.PatientDataDto
import ca.bc.gov.data.datasource.remote.api.HealthGatewayPrivateApi
import ca.bc.gov.data.datasource.remote.model.base.patientdata.PatientDataRequestType
import ca.bc.gov.data.model.mapper.toDto
import ca.bc.gov.data.utils.safeCall
import javax.inject.Inject

/**
 * @author Pinakin Kansara
 * [PatientServicesRemoteDataSource] used to fetch patient service
 * from API = v2.
 * Below are the service data we are able to fetch at the moment
 * - Organ Donor
 * - Diagnostic Imaging
 */
class PatientServicesRemoteDataSource @Inject constructor(
    private val healthGatewayPrivateApi: HealthGatewayPrivateApi
) {

    /**
     * This API is used to get data for
     * @see [PatientDataRequestType.ORGAN_DONOR] and [PatientDataRequestType.DIAGNOSTIC_IMAGING]
     * Uses API = v2
     */
    suspend fun getPatientData(hdId: String): List<PatientDataDto> {
        val response = safeCall {
            healthGatewayPrivateApi.getPatientData(
                hdid = hdId,
                patientDataTypes = listOf(
                    PatientDataRequestType.ORGAN_DONOR.value,
                    PatientDataRequestType.DIAGNOSTIC_IMAGING.value
                )
            )
        } ?: throw MyHealthException(SERVER_ERROR, MESSAGE_INVALID_RESPONSE)

        return response.toDto()
    }

    suspend fun getPatientDataFile(hdid: String, fileId: String): String {
        val response = safeCall {
            healthGatewayPrivateApi.getPatientDataFile(hdid, fileId)
        } ?: throw MyHealthException(SERVER_ERROR, MESSAGE_INVALID_RESPONSE)

        val result = response.content.foldIndexed(ByteArray(response.content.size)) { index, bytes, value ->
            bytes.apply {
                set(
                    index,
                    value.toByte()
                )
            }
        }
        return Base64.encodeToString(result, Base64.DEFAULT)
    }
}

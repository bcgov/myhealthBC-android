package ca.bc.gov.repository

import ca.bc.gov.data.ImmunizationRemoteDataSource
import ca.bc.gov.data.remote.model.request.VaccineStatusRequest
import ca.bc.gov.repository.model.PatientVaccineRecord
import ca.bc.gov.repository.qr.ProcessQrRepository
import ca.bc.gov.repository.qr.VaccineRecordState
import ca.bc.gov.repository.utils.Base64ToInputImageConverter
import javax.inject.Inject

/**
 * @author Pinakin Kansara
 */
class FetchVaccineRecordRepository @Inject constructor(
    private val base64ToInputImageConverter: Base64ToInputImageConverter,
    private val immunizationRemoteDataSource: ImmunizationRemoteDataSource,
    private val processQrRepository: ProcessQrRepository
) {

    suspend fun fetchVaccineRecord(
        phn: String,
        dob: String,
        dov: String
    ): Pair<VaccineRecordState, PatientVaccineRecord?> {
        val response =
            immunizationRemoteDataSource.getVaccineStatus(VaccineStatusRequest(phn, dob, dov))
        val image = base64ToInputImageConverter.convert(response.payload?.qrCode?.data!!)
        val patientVaccineRecord = processQrRepository.processQrCode(image)
        val (status, record) = patientVaccineRecord
        record?.vaccineRecordDto?.federalPass = response.payload?.federalVaccineProof?.data
        record?.patientDto?.phn = response.payload?.phn
        return Pair(status, record)
    }

    suspend fun fetchAuthenticatedVaccineRecord(
        token: String, hdid: String
    ): Pair<VaccineRecordState, PatientVaccineRecord?> {
        val response =
            immunizationRemoteDataSource.getAuthenticatedVaccineStatus(token, hdid)
        val image = base64ToInputImageConverter.convert(response.payload?.qrCode?.data!!)
        val patientVaccineRecord = processQrRepository.processQrCode(image)
        val (status, record) = patientVaccineRecord
        record?.vaccineRecordDto?.federalPass = response.payload?.federalVaccineProof?.data
        record?.patientDto?.phn = response.payload?.phn
        return Pair(status, record)
    }
}

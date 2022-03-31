package ca.bc.gov.repository

import ca.bc.gov.data.datasource.remote.ImmunizationRemoteDataSource
import ca.bc.gov.data.datasource.remote.model.request.VaccineStatusRequest
import ca.bc.gov.data.model.VaccineStatus
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
        return processResponse(response)
    }

    suspend fun fetchVaccineRecord(
        token: String,
        hdid: String
    ): Pair<VaccineRecordState, PatientVaccineRecord?> {
        val response =
            immunizationRemoteDataSource.getVaccineStatus(token, hdid)
        return processResponse(response)
    }

    private suspend fun processResponse(vaccineStatus: VaccineStatus): Pair<VaccineRecordState, PatientVaccineRecord?> {
        val image = base64ToInputImageConverter.convert(vaccineStatus.qrCode.data)
        val patientVaccineRecord = processQrRepository.processQrCode(image)
        val (status, record) = patientVaccineRecord
        record?.vaccineRecordDto?.federalPass = vaccineStatus.federalVaccineProof.data
        record?.patientDto?.phn = vaccineStatus.phn
        return Pair(status, record)
    }
}

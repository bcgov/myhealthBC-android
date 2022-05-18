package ca.bc.gov.repository.qr

import android.net.Uri
import ca.bc.gov.data.model.mapper.toEntity
import ca.bc.gov.repository.extensions.toPatient
import ca.bc.gov.repository.extensions.toPatientVaccineRecord
import ca.bc.gov.repository.model.PatientVaccineRecord
import ca.bc.gov.repository.patient.PatientRepository
import ca.bc.gov.repository.scanner.QrScanner
import ca.bc.gov.repository.utils.UriToImage
import ca.bc.gov.shcdecoder.SHCVerifier
import ca.bc.gov.shcdecoder.model.VaccinationStatus
import com.google.mlkit.vision.common.InputImage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * @author Pinakin Kansara
 */
class ProcessQrRepository @Inject constructor(
    private val qrScanner: QrScanner,
    private val uriToImage: UriToImage,
    private val shcVerifier: SHCVerifier,
    private val patientRepository: PatientRepository
) {

    suspend fun processQrCode(image: InputImage): Pair<VaccineRecordState, PatientVaccineRecord?> {
        val shcUri = qrScanner.process(image) ?: throw Exception("")
        return processQRCode(shcUri)
    }

    suspend fun processQRCode(uri: Uri): Pair<VaccineRecordState, PatientVaccineRecord?> {
        val image = uriToImage.imageFromFile(uri)
        val shcUri = qrScanner.process(image) ?: throw Exception("")
        return processQRCode(shcUri)
    }

    suspend fun processQRCode(shcUri: String): Pair<VaccineRecordState, PatientVaccineRecord?> {
        try {
            if (!shcVerifier.hasValidSignature(shcUri)) {
                return Pair(VaccineRecordState.INVALID, null)
            }
            val (status, shcData) = shcVerifier.getStatus(shcUri)
            if (status == VaccinationStatus.INVALID || status == VaccinationStatus.NOT_VACCINATED) {
                return Pair(VaccineRecordState.INVALID, null)
            }
            val patient = shcData.toPatient()
            val patientsVaccineRecord =
                withContext(Dispatchers.IO) {
                    patientRepository.getPatientWithVaccineAndDoses(
                        patient.toEntity()
                    )
                }
            val patientData = shcData.toPatientVaccineRecord(shcUri, status)
            if (patientsVaccineRecord.isNullOrEmpty() || patientsVaccineRecord.last().vaccineWithDoses == null) {
                return Pair(VaccineRecordState.CAN_INSERT, patientData)
            }
            val record = patientsVaccineRecord.last()
            val value =
                patientData.vaccineRecordDto.qrIssueDate.compareTo(record.vaccineWithDoses?.vaccine?.qrIssueDate!!)
            return if (value > 0) {
                patientData.patientDto.id = record.patient.id
                patientData.vaccineRecordDto.id = record.vaccineWithDoses?.vaccine?.id!!
                patientData.vaccineRecordDto.patientId =
                    record.vaccineWithDoses?.vaccine?.patientId!!
                Pair(VaccineRecordState.CAN_UPDATE, patientData)
            } else {
                Pair(VaccineRecordState.DUPLICATE, patientData)
            }
        } catch (e: Exception) {
            return Pair(VaccineRecordState.INVALID, null)
        }
    }
}

enum class VaccineRecordState {
    CAN_UPDATE,
    CAN_INSERT,
    DUPLICATE,
    INVALID
}

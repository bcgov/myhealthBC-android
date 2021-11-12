package ca.bc.gov.bchealth.repository

import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import ca.bc.gov.bchealth.data.local.entity.HealthCard
import ca.bc.gov.bchealth.datasource.LocalDataSource
import ca.bc.gov.bchealth.model.HealthCardDto
import ca.bc.gov.bchealth.model.ImmunizationStatus
import ca.bc.gov.bchealth.model.network.responses.vaccinestatus.VaxStatusResponse
import ca.bc.gov.bchealth.services.ImmunizationServices
import ca.bc.gov.bchealth.utils.ErrorData
import ca.bc.gov.bchealth.utils.Response
import ca.bc.gov.bchealth.utils.SHCDecoder
import ca.bc.gov.bchealth.utils.getDateTime
import com.google.mlkit.vision.barcode.Barcode
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking

/**
 * [CardRepository]
 *
 * @author Pinakin Kansara
 */
class CardRepository @Inject constructor(
    private val dataSource: LocalDataSource,
    private val shcDecoder: SHCDecoder,
    private val immunizationServices: ImmunizationServices,
) {

    /*
    * Used to manage Success, Error and Loading status in the UI
    * */
    private val responseMutableSharedFlow = MutableSharedFlow<Response<String>>()
    val responseSharedFlow: SharedFlow<Response<String>>
        get() = responseMutableSharedFlow.asSharedFlow()

    /*
    * Health passes fetched from DB
    * */
    val cards: Flow<List<HealthCardDto>> = dataSource.getCards().map { healthCards ->
        healthCards.map { card ->
            try {
                val data = shcDecoder.getImmunizationStatus(card.uri)
                HealthCardDto(
                    card.id,
                    data.name,
                    data.status,
                    card.uri,
                    false,
                    data.issueDate.getDateTime(),
                    data.birthDate.toString(),
                    data.occurrenceDateTime.toString(),
                    card.federalPass
                )
            } catch (e: Exception) {
                HealthCardDto(
                    0, "", ImmunizationStatus.INVALID_QR_CODE, card.uri,
                    false
                )
            }
        }
    }

    /*
    * Insert or update existing health pass
    * */
    suspend fun insert(card: HealthCard) {
        try {
            val healthPassToBeInserted = shcDecoder.getImmunizationStatus(card.uri)

            val cards = dataSource.getCards().firstOrNull()

            if (cards.isNullOrEmpty()) {
                dataSource.insert(card)
                responseMutableSharedFlow.emit(Response.Success())
            } else {

                val existingHealthPass = cards.filter { record ->
                    val immunizationRecord = shcDecoder.getImmunizationStatus(record.uri)
                    (
                        immunizationRecord.name == healthPassToBeInserted.name &&
                            immunizationRecord.birthDate == healthPassToBeInserted.birthDate
                        )
                }

                if (existingHealthPass.isNullOrEmpty()) {
                    dataSource.insert(card)
                    responseMutableSharedFlow.emit(Response.Success())
                } else {
                    existingHealthPass.forEach { existingHealthCard ->

                        val existingHealthPassStatus = shcDecoder
                            .getImmunizationStatus(existingHealthCard.uri).status

                        if (healthPassToBeInserted.status == existingHealthPassStatus) {
                            responseMutableSharedFlow
                                .emit(
                                    Response.Error(ErrorData.EXISTING_QR)
                                )
                            return@forEach
                        }

                        if (existingHealthPassStatus == ImmunizationStatus.PARTIALLY_IMMUNIZED) {
                            existingHealthCard.uri = card.uri
                            existingHealthCard.federalPass = card.federalPass
                            dataSource.update(existingHealthCard)
                            responseMutableSharedFlow.emit(Response.Success())
                        } else if (existingHealthPassStatus == ImmunizationStatus.FULLY_IMMUNIZED) {
                            responseMutableSharedFlow
                                .emit(
                                    Response.Error(
                                        ErrorData.FULLY_VACCINATED_QR_EXISTS
                                    )
                                )
                        }
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            responseMutableSharedFlow
                .emit(Response.Error(ErrorData.GENERIC_ERROR))
        }
    }

    suspend fun unLink(card: HealthCard) = dataSource.unLink(card)

    suspend fun rearrangeHealthCards(cards: List<HealthCard>) = dataSource.rearrange(cards)

    private suspend fun saveCard(uri: String, base64EncodedPdf: String) {
        insert(HealthCard(uri = uri, federalPass = base64EncodedPdf))
    }

    /*
    * Used in uploading the QR from gallery
    * */
    suspend fun processUploadedImage(
        uri: Uri,
        context: Context
    ) {

        var image: InputImage? = null
        try {
            image = InputImage.fromFilePath(context, uri) // TODO: 24/09/21 yet to handle warning
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
            responseMutableSharedFlow.emit(Response.Error(ErrorData.GENERIC_ERROR))
            return
        }

        processImage(image, "")
    }

    /*
    * HGS vaccine status API provides Base64 encoded image data.
    * Get the QR image from this data.
    * */
    private suspend fun prepareQRImage(base64EncodedImage: String, base64EncodedPdf: String) {
        val decodedByteArray: ByteArray =
            Base64
                .decode(base64EncodedImage, Base64.DEFAULT)
        val decodedBitmap = BitmapFactory.decodeByteArray(
            decodedByteArray,
            0,
            decodedByteArray.size
        )

        var image: InputImage? = null
        image = InputImage
            .fromBitmap(decodedBitmap, 0)

        processImage(image, base64EncodedPdf)
    }

    /*
    * Process QR image and get the shcUri
    * */
    private suspend fun processImage(
        image: InputImage,
        base64EncodedPdf: String
    ) {

        val scanner = BarcodeScanning.getClient()

        scanner.process(image)
            .addOnSuccessListener { barcodes ->
                barcodes.firstOrNull().let { barcode ->

                    if (barcode == null) {
                        runBlocking {
                            responseMutableSharedFlow.emit(Response.Error(ErrorData.INVALID_QR))
                        }
                        return@let
                    }

                    if (barcode.format != Barcode.FORMAT_QR_CODE) {
                        runBlocking {
                            responseMutableSharedFlow.emit(Response.Error(ErrorData.INVALID_QR))
                        }
                        return@let
                    }

                    val rawValue = barcode.rawValue
                    rawValue?.let {
                        runBlocking {
                            processShcUri(it, base64EncodedPdf)
                        }
                    }
                }
            }
            .addOnFailureListener {
                runBlocking {
                    responseMutableSharedFlow.emit(Response.Error(ErrorData.INVALID_QR))
                }
            }
            .addOnCompleteListener {
                println("Scan finished!")
            }
    }

    /*
    * Find the vaccination status and save the vaccine data for future use.
    * */
    private suspend fun processShcUri(
        shcUri: String,
        base64EncodedPdf: String
    ) {
        try {
            when (shcDecoder.getImmunizationStatus(shcUri).status) {
                ImmunizationStatus.FULLY_IMMUNIZED,
                ImmunizationStatus.PARTIALLY_IMMUNIZED -> {
                    saveCard(shcUri, base64EncodedPdf)
                }

                ImmunizationStatus.INVALID_QR_CODE -> {
                    responseMutableSharedFlow.emit(Response.Error(ErrorData.INVALID_QR))
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            responseMutableSharedFlow.emit(Response.Error(ErrorData.INVALID_QR))
        }
    }

    /*
    * HGS connection to fetch vaccination status
    * */
    suspend fun getVaccineStatus(phn: String, dob: String, dov: String) {
        responseMutableSharedFlow.emit(Response.Loading())
        val result = immunizationServices.getVaccineStatus(
            phn, dob, dov
        )

        if (validateResponse(result)) {
            val vaxStatusResponse = result.body()

            vaxStatusResponse?.resourcePayload?.qrCode?.data
                ?.let { base64EncodedImage ->

                    vaxStatusResponse.resourcePayload.federalVaccineProof.data
                        ?.let { base64EncodedPdf ->

                            try {
                                prepareQRImage(base64EncodedImage, base64EncodedPdf)
                            } catch (e: Exception) {
                                responseMutableSharedFlow
                                    .emit(Response.Error(ErrorData.GENERIC_ERROR))
                            }
                        }
                }
        } else {
            result.body()?.resultError?.resultMessage?.let {
                val errorData = ErrorData.NETWORK_ERROR
                errorData.errorMessage = it
                responseMutableSharedFlow.emit(Response.Error(errorData))
                return
            }
            responseMutableSharedFlow
                .emit(Response.Error(ErrorData.GENERIC_ERROR))
        }
    }

    private fun validateResponse(result: retrofit2.Response<VaxStatusResponse>): Boolean {

        if (!result.isSuccessful)
            return false

        val vaxStatusResponse: VaxStatusResponse = result.body() ?: return false

        if (vaxStatusResponse.resultError != null)
            return false

        if (vaxStatusResponse.resourcePayload.qrCode.data.isNullOrEmpty())
            return false

        if (vaxStatusResponse.resourcePayload.federalVaccineProof.data.isNullOrEmpty())
            return false

        return true
    }

    /*
    * Fetch federal travel pass for existing vaccine cards
    * */
    suspend fun getFederalTravelPass(healthCardDto: HealthCardDto, phn: String) {
        responseMutableSharedFlow.emit(Response.Loading())

        val result = immunizationServices.getVaccineStatus(
            phn,
            healthCardDto.birthDate,
            healthCardDto.occurrenceDateTime
        )

        if (validateResponse(result)) {
            val vaxStatusResponse = result.body()

            vaxStatusResponse?.resourcePayload?.federalVaccineProof?.data
                ?.let { base64EncodedPdf ->

                    try {
                        dataSource.update(
                            HealthCard(
                                healthCardDto.id,
                                healthCardDto.uri,
                                base64EncodedPdf
                            )
                        )
                        healthCardDto.federalPass = base64EncodedPdf
                        responseMutableSharedFlow.emit(Response.Success(data = healthCardDto))
                    } catch (e: Exception) {
                        responseMutableSharedFlow
                            .emit(Response.Error(ErrorData.GENERIC_ERROR))
                    }
                }
        } else {
            result.body()?.resultError?.resultMessage?.let {
                val errorData = ErrorData.NETWORK_ERROR
                errorData.errorMessage = it
                responseMutableSharedFlow.emit(Response.Error(errorData))
                return
            }
            responseMutableSharedFlow
                .emit(Response.Error(ErrorData.GENERIC_ERROR))
        }
    }
}

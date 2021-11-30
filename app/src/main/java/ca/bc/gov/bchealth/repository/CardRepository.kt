package ca.bc.gov.bchealth.repository

import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import ca.bc.gov.bchealth.data.local.entity.HealthCard
import ca.bc.gov.bchealth.datasource.LocalDataSource
import ca.bc.gov.bchealth.model.HealthCardDto
import ca.bc.gov.bchealth.model.ImmunizationRecord
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
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

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
                    card.uri,
                    card.federalPass,
                    data.name,
                    data.status,
                    false,
                    data.issueDate.getDateTime(),
                    data.birthDate.toString(),
                    data.occurrenceDateTime.toString(),
                    data.immunizationEntries
                )
            } catch (e: Exception) {
                HealthCardDto(
                    0, card.uri, "", "", ImmunizationStatus.INVALID_QR_CODE,
                    false, "", "", "",
                    null
                )
            }
        }
    }

    /*
    * Used in uploading the QR from gallery
    * */
    suspend fun processUploadedImage(
        uri: Uri,
        context: Context
    ) {

        kotlin.runCatching {
            var image: InputImage? = null
            try {
                image = InputImage.fromFilePath(context, uri)
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
                responseMutableSharedFlow.emit(Response.Error(ErrorData.GENERIC_ERROR))
                return
            }

            processImage(image, "", "")
        }
    }

    /*
    * HGS vaccine status API provides Base64 encoded image data.
    * Get the QR image from this data.
    * */
    private suspend fun prepareQRImage(
        base64EncodedImage: String,
        base64EncodedPdf: String,
        healthPassTobeUpdated: String
    ) {
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

        processImage(image, base64EncodedPdf, healthPassTobeUpdated)
    }

    /*
    * Process QR image and get the shcUri
    * */
    private suspend fun processImage(
        image: InputImage,
        base64EncodedPdf: String,
        healthPassTobeUpdated: String
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
                            processShcUri(it, base64EncodedPdf, healthPassTobeUpdated)
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
        base64EncodedPdf: String,
        healthPassTobeUpdated: String
    ) {
        try {
            when (shcDecoder.getImmunizationStatus(shcUri).status) {
                ImmunizationStatus.FULLY_IMMUNIZED,
                ImmunizationStatus.PARTIALLY_IMMUNIZED -> {
                    saveCard(shcUri, base64EncodedPdf, healthPassTobeUpdated)
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

    private suspend fun saveCard(
        uri: String,
        base64EncodedPdf: String,
        healthPassTobeUpdated: String
    ) {
        insert(HealthCard(uri = uri, federalPass = base64EncodedPdf), healthPassTobeUpdated)
    }

    suspend fun insert(healthCard: HealthCard) {
        insert(healthCard, "")
    }

    /*
    * Insert or update existing health pass.
    * 1. If there are no health passes, then insert the health pass directly
    * 2. If user is adding Federal Travel proof to the existing health pass, replace the health pass without any condition (HAPP-235)
    * 3. If there is a health pass which matches with user's name and DOB, and is older compared to existing health pass, then inform user as already a health pass exists.
    * 4. If there is a health pass which matches with user's name and DOB, and is recent compared to existing health pass, then replace the health pass.
    * */
    suspend fun insert(healthCard: HealthCard, healthPassTobeUpdated: String) {
        try {
            val healthPassToBeInserted = shcDecoder.getImmunizationStatus(healthCard.uri)

            val cards = dataSource.getCards().firstOrNull()

            if (cards.isNullOrEmpty()) {
                dataSource.insert(healthCard)
                responseMutableSharedFlow.emit(Response.Success(Pair(healthCard, false)))
                return
            }
            if (healthPassTobeUpdated.isNotEmpty()) {
                healthCard.id = healthPassTobeUpdated.toInt()
                responseMutableSharedFlow
                    .emit(Response.Success(Pair(healthCard, true)))
            } else {

                val filteredHealthCard = cards.filter { record ->
                    val immunizationRecord = shcDecoder.getImmunizationStatus(record.uri)
                    (
                            immunizationRecord.name.lowercase()
                                    == healthPassToBeInserted.name.lowercase() &&
                                    immunizationRecord.birthDate
                                    == healthPassToBeInserted.birthDate
                            )
                }

                if (filteredHealthCard.isNullOrEmpty()) {
                    dataSource.insert(healthCard)
                    responseMutableSharedFlow.emit(Response.Success(Pair(healthCard, false)))
                } else {
                    updateHealthPass(
                        healthCard,
                        filteredHealthCard,
                        healthPassToBeInserted
                    )
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            responseMutableSharedFlow
                .emit(Response.Error(ErrorData.GENERIC_ERROR))
        }
    }

    private suspend fun updateHealthPass(
        healthCard: HealthCard,
        filteredHealthCard: List<HealthCard>,
        healthPassToBeInserted: ImmunizationRecord
    ) {

        // Ref: https://freshworks.atlassian.net/browse/HAPP-173

        filteredHealthCard.forEach { existingHealthCard ->

            val existingImmuRecord = shcDecoder.getImmunizationStatus(existingHealthCard.uri)

            if (existingImmuRecord.issueDate >= healthPassToBeInserted.issueDate) {
                responseMutableSharedFlow.emit(Response.Error(ErrorData.EXISTING_QR))
            } else {
                existingHealthCard.uri = healthCard.uri
                existingHealthCard.federalPass = healthCard.federalPass
                responseMutableSharedFlow.emit(Response.Success(Pair(existingHealthCard, true)))
            }
        }
    }

    suspend fun replaceExitingHealthPass(healthCard: HealthCard) {
        dataSource.update(healthCard)
    }

    suspend fun unLink(card: HealthCard) = dataSource.unLink(card)

    suspend fun rearrangeHealthCards(cards: List<HealthCard>) = dataSource.rearrange(cards)

    /*
    * HGS connection to fetch vaccination status
    * */
    suspend fun getVaccineStatus(phn: String, dob: String, dov: String) {
        responseMutableSharedFlow.emit(Response.Loading())

        loop@ for (i in RETRY_COUNT downTo 1) {

            val result = immunizationServices.getVaccineStatus(
                phn, dob, dov
            )

            if (validateResponse(result)) {
                val vaxStatusResponse = result.body()

                /*
                 * Loaded field will return false when HGS will respond with cache data.
                 * HGS response also provide the retry time after which updated data is available.
                 * */
                if (vaxStatusResponse?.resourcePayload?.loaded == false) {

                    vaxStatusResponse.resourcePayload.retryin.toLong().let {
                        delay(it)
                    }

                    if (i == 1) {
                        responseMutableSharedFlow.emit(Response.Error(ErrorData.GENERIC_ERROR))
                        break@loop
                    } else {
                        continue@loop
                    }
                } else {

                    vaxStatusResponse?.resourcePayload?.qrCode?.data?.let { base64EncodedQrImage ->
                        vaxStatusResponse.resourcePayload.federalVaccineProof.data
                            ?.let { base64EncodedFederalPassPdf ->

                                prepareQRImage(
                                    base64EncodedQrImage,
                                    base64EncodedFederalPassPdf,
                                    ""
                                )
                                return
                            }
                    }
                    responseMutableSharedFlow.emit(Response.Error(ErrorData.GENERIC_ERROR))
                }
            } else {
                responseMutableSharedFlow.emit(Response.Error(ErrorData.GENERIC_ERROR))
            }
            break@loop
        }
    }

    private fun validateResponse(result: retrofit2.Response<VaxStatusResponse>): Boolean {

        if (!result.isSuccessful)
            return false

        if (result.body() == null)
            return false

        val vaxStatusResponse = result.body()
        if (vaxStatusResponse?.resourcePayload == null)
            return false

        return true
    }

    /*
    * Fetch federal travel pass for existing vaccine cards
    * */
    suspend fun getFederalTravelPass(healthCardDto: HealthCardDto, phn: String) {
        responseMutableSharedFlow.emit(Response.Loading())

        loop@ for (i in RETRY_COUNT downTo 1) {
            val result = immunizationServices.getVaccineStatus(
                phn,
                healthCardDto.birthDate,
                healthCardDto.occurrenceDateTime
            )

            if (validateResponse(result)) {
                val vaxStatusResponse = result.body()

                /*
                * Loaded field will return false when HGS will respond with cache data.
                * HGS response also provide the retry time after which updated data is available.
                * */
                if (vaxStatusResponse?.resourcePayload?.loaded == false) {

                    vaxStatusResponse.resourcePayload.retryin.toLong().let {
                        delay(it)
                    }

                    if (i == 1) {
                        responseMutableSharedFlow.emit(Response.Error(ErrorData.GENERIC_ERROR))
                        break@loop
                    } else {
                        continue@loop
                    }
                } else {

                    vaxStatusResponse?.resourcePayload?.qrCode?.data?.let { base64EncodedQrImage ->
                        vaxStatusResponse.resourcePayload.federalVaccineProof.data
                            ?.let { base64EncodedFederalPassPdf ->

                                prepareQRImage(
                                    base64EncodedQrImage,
                                    base64EncodedFederalPassPdf,
                                    healthCardDto.id.toString()
                                )
                                return
                            }
                    }
                    responseMutableSharedFlow.emit(Response.Error(ErrorData.GENERIC_ERROR))
                }
            } else {
                responseMutableSharedFlow.emit(Response.Error(ErrorData.GENERIC_ERROR))
            }
            break@loop
        }
    }

    companion object {
        const val RETRY_COUNT = 3
    }
}

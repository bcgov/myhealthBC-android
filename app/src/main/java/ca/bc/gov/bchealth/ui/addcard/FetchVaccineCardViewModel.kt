package ca.bc.gov.bchealth.ui.addcard

import android.graphics.BitmapFactory
import android.util.Base64
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ca.bc.gov.bchealth.data.local.entity.HealthCard
import ca.bc.gov.bchealth.model.ImmunizationStatus
import ca.bc.gov.bchealth.model.network.responses.vaccinestatus.VaxStatusResponse
import ca.bc.gov.bchealth.repository.CardRepository
import ca.bc.gov.bchealth.utils.Response
import ca.bc.gov.bchealth.utils.SHCDecoder
import com.google.mlkit.vision.barcode.Barcode
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

/*
* Created by amit_metri on 18,October,2021
*/
@HiltViewModel
class FetchVaccineCardViewModel @Inject constructor(
    private val shcDecoder: SHCDecoder,
    private val repository: CardRepository
) : ViewModel() {

    val vaxStatusResponseLiveData: Flow<Response<VaxStatusResponse>>
        get() = repository.vaxStatusResponseLiveData

    suspend fun getVaccineStatus(phn: String, dob: String, dov: String) {
        repository.getVaccineStatus(phn, dob, dov)
    }

    val uploadStatus = MutableLiveData<Boolean>()

    /*
    * HGS vaccine status API provides Base64 encoded image data.
    * Get the QR image from this data.
    * */
    fun saveVaccineCard(base64EncodedImage: String){
        prepareQRImage(base64EncodedImage)
    }

    private fun prepareQRImage(base64EncodedImage: String) {
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

        processImage(image)
    }

    /*
    * Process QR image and get the shcUri
    * */
    private fun processImage(
        image: InputImage
    ) = viewModelScope.launch {

        val scanner = BarcodeScanning.getClient()

        scanner.process(image)
            .addOnSuccessListener { barcodes ->
                barcodes.firstOrNull().let { barcode ->

                    if (barcode == null) {
                        uploadStatus.value = false
                        return@let
                    }

                    if (barcode.format != Barcode.FORMAT_QR_CODE) {
                        uploadStatus.value = false
                        return@let
                    }

                    val rawValue = barcode.rawValue
                    rawValue?.let {
                        processShcUri(it)
                    }
                }
            }
            .addOnFailureListener {
                uploadStatus.value = false
            }
            .addOnCompleteListener {
                println("Scan finished!")
            }
    }

    /*
    * Find the vaccination status and save the vaccine data for future use.
    * */
    private fun processShcUri(
        shcUri: String
    ) = viewModelScope.launch {
        try {
            when (shcDecoder.getImmunizationStatus(shcUri).status) {
                ImmunizationStatus.FULLY_IMMUNIZED,
                ImmunizationStatus.PARTIALLY_IMMUNIZED -> {
                    saveCard(shcUri)
                    uploadStatus.value = true
                }

                ImmunizationStatus.INVALID_QR_CODE -> {
                    uploadStatus.value = false
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            uploadStatus.value = false
        }
    }

    private fun saveCard(uri: String) = viewModelScope.launch {
        repository.insert(HealthCard(uri = uri))
    }
}

package ca.bc.gov.bchealth.ui.addcard

import android.content.Context
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ca.bc.gov.bchealth.data.local.entity.HealthCard
import ca.bc.gov.bchealth.http.MustBeQueued
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
import javax.inject.Inject
import kotlinx.coroutines.launch

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

    fun processImage(
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

    fun processShcUri(
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

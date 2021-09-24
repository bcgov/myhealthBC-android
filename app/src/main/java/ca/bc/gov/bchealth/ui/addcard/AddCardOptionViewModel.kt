package ca.bc.gov.bchealth.ui.addcard

import android.content.Context
import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ca.bc.gov.bchealth.data.local.entity.CardType
import ca.bc.gov.bchealth.data.local.entity.HealthCard
import ca.bc.gov.bchealth.model.ImmunizationStatus
import ca.bc.gov.bchealth.repository.CardRepository
import ca.bc.gov.bchealth.utils.SHCDecoder
import com.google.mlkit.vision.barcode.Barcode
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.launch

/**
 * [AddCardOptionViewModel]
 *
 * @author amit metri
 */
@HiltViewModel
class AddCardOptionViewModel @Inject constructor(
    private val shcDecoder: SHCDecoder,
    private val repository: CardRepository
) : ViewModel() {

    val uploadStatus = MutableLiveData<Boolean>()

    fun processUploadedImage(
        uri: Uri,
        context: Context
    ) = viewModelScope.launch {

        var image: InputImage? = null
        try {
            image = InputImage.fromFilePath(context, uri) // TODO: 24/09/21 yet to handle warning
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
            uploadStatus.value = false
            return@launch
        }

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

    private fun processShcUri(
        shcUri: String
    ) = viewModelScope.launch {
        try {
            val status = shcDecoder.getImmunizationStatus(shcUri)
            when (status.second) {
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
            uploadStatus.value = false
        }
    }

    private fun saveCard(uri: String) = viewModelScope.launch {
        repository.insertHealthCard(HealthCard(uri, CardType.QR))
    }
}

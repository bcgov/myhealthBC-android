package ca.bc.gov.bchealth.ui.addcard

import android.content.Context
import android.net.Uri
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
import kotlinx.coroutines.launch
import javax.inject.Inject

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

    fun processUploadedImage(
        uri: Uri,
        context: Context,
        listener: AddCardOptionFragment.UploadResultListener
    ) = viewModelScope.launch {

        var image: InputImage? = null
        try {
            image = InputImage.fromFilePath(context, uri) // TODO: 24/09/21 yet to handle warning 
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
            listener.onFailure()
            return@launch
        }

        val scanner = BarcodeScanning.getClient()

        scanner.process(image)
            .addOnSuccessListener { barcodes ->
                barcodes.firstOrNull().let { barcode ->

                    if (barcode == null) {
                        listener.onFailure()
                        return@let
                    }

                    if (barcode.format != Barcode.FORMAT_QR_CODE) {
                        listener.onFailure()
                        return@let
                    }

                    val rawValue = barcode.rawValue
                    rawValue?.let {
                        processShcUri(it, listener)
                    }
                }
            }
            .addOnFailureListener {
                listener.onFailure()
            }
            .addOnCompleteListener {
                println("Scan finished!")
            }
    }

    private fun processShcUri(
        shcUri: String,
        listener: AddCardOptionFragment.UploadResultListener
    ) = viewModelScope.launch {
        try {
            val status = shcDecoder.getImmunizationStatus(shcUri)
            when (status.second) {
                ImmunizationStatus.FULLY_IMMUNIZED,
                ImmunizationStatus.PARTIALLY_IMMUNIZED -> {
                    saveCard(shcUri)
                    listener.onSuccess()
                }

                ImmunizationStatus.INVALID_QR_CODE -> {
                    listener.onFailure()
                }
            }
        } catch (e: Exception) {
            listener.onFailure()
        }
    }

    private fun saveCard(uri: String) = viewModelScope.launch {
        repository.insertHealthCard(HealthCard(uri, CardType.QR))
    }
}
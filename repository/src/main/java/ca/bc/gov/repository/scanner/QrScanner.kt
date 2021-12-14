package ca.bc.gov.repository.scanner

import com.google.mlkit.vision.barcode.BarcodeScanner
import com.google.mlkit.vision.common.InputImage
import javax.inject.Inject

/**
 * @author Pinakin Kansara
 */
class QrScanner @Inject constructor(
    private val scanner: BarcodeScanner
) {

    suspend fun process(image: InputImage) = scanner.awaitProcess(image)
}
package ca.bc.gov.repository.scanner

import com.google.mlkit.vision.barcode.BarcodeScanner
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

suspend fun BarcodeScanner.awaitProcess(image: InputImage): String? =
    suspendCancellableCoroutine<String?> { continuation ->

        process(image)
            .addOnSuccessListener { barcodes ->
                barcodes.firstOrNull().let { barcode ->
                    if (barcode == null ||
                        (barcode.format != Barcode.FORMAT_QR_CODE) ||
                        barcode.rawValue == null
                    ) {
                        continuation.resume(null)
                    }

                    barcode?.rawValue?.let {
                        continuation.resume(it)
                    }
                }
            }.addOnFailureListener {
                continuation.resumeWithException(it)
            }
    }

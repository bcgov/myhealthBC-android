package ca.bc.gov.repository.utils

import android.graphics.BitmapFactory
import android.util.Base64
import com.google.mlkit.vision.common.InputImage

/**
 * @author Pinakin Kansara
 */
class Base64ToInputImageConverter {

    suspend fun convert(base64String: String): InputImage {
        val decodedByteArray = Base64
            .decode(base64String, Base64.DEFAULT)
        val decodedBitmap = BitmapFactory.decodeByteArray(
            decodedByteArray,
            0,
            decodedByteArray.size
        )
        return InputImage
            .fromBitmap(decodedBitmap, 0)
    }
}
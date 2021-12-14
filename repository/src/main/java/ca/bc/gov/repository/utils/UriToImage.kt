package ca.bc.gov.repository.utils

import android.content.Context
import android.net.Uri
import com.google.mlkit.vision.common.InputImage
import javax.inject.Inject

/**
 * @author Pinakin Kansara
 */
class UriToImage @Inject constructor(
    private val context: Context
) {

    suspend fun imageFromFile(uri: Uri): InputImage = InputImage.fromFilePath(context, uri)
}
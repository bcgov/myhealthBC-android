package ca.bc.gov.repository

import android.content.Context
import android.util.Base64
import java.io.File
import java.io.File.createTempFile
import javax.inject.Inject

/**
 * @author Pinakin Kansara
 */
class PdfDecoderRepository @Inject constructor(
    private val context: Context,
) {

    suspend fun base64ToPDF(base64String: String): File {
        val byteArray = Base64.decode(base64String, Base64.DEFAULT)
        val file = createTempFile("file", ".pdf", context.filesDir)
        context.openFileOutput(file.name, Context.MODE_PRIVATE).use {
            it.write(byteArray)
        }
        return file
    }
}

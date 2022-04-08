package ca.bc.gov.bchealth.utils

import android.content.Intent
import android.net.Uri
import androidx.activity.result.ActivityResultLauncher
import androidx.core.content.FileProvider
import androidx.fragment.app.FragmentActivity
import java.io.File

/*
* Created by amit_metri on 07,April,2022
*/
class PdfHelper {

    fun showPDF(
        file: File,
        requireActivity: FragmentActivity,
        resultListener: ActivityResultLauncher<Intent>
    ) {
        val authority =
            requireActivity.applicationContext.packageName.toString() +
                ".fileprovider"
        val uriToFile: Uri =
            FileProvider.getUriForFile(requireActivity, authority, file)
        val shareIntent = Intent(Intent.ACTION_VIEW)
        shareIntent.setDataAndType(uriToFile, "application/pdf")
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        resultListener.launch(shareIntent)
    }
}

package ca.bc.gov.bchealth.utils

import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import ca.bc.gov.bchealth.R

fun Activity.composeEmail(address: String, subject: String) {
    val intent = Intent(Intent.ACTION_SENDTO).apply {
        data = Uri.parse("mailto:")
        putExtra(Intent.EXTRA_EMAIL, arrayOf(address))
        putExtra(Intent.EXTRA_SUBJECT, subject)
    }

    if (intent.resolveActivity(packageManager) != null) {
        startActivity(intent)
    } else {
        copyToClipboard(address)
        // Google strongly recommends removing any pop-up widget shown after an in-app copy for Android 13 and higher.
        // https://developer.android.com/develop/ui/views/touch-and-input/copy-paste#duplicate-notifications
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.S_V2) {
            toast(getString(R.string.email_copied_to_clipboard))
        }
    }
}

private fun Activity.copyToClipboard(text: String) {
    val clipboardManager = getSystemService(AppCompatActivity.CLIPBOARD_SERVICE) as ClipboardManager
    clipboardManager.setPrimaryClip(ClipData.newPlainText("", text))
}

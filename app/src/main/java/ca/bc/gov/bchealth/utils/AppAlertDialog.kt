package ca.bc.gov.bchealth.utils

import android.content.Context
import androidx.appcompat.app.AlertDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder

object AppAlertDialog {
    private lateinit var dialogAlert: AlertDialog

    private fun isDialogShowing(): Boolean =
        this::dialogAlert.isInitialized && dialogAlert.isShowing

    fun showConfirmationAlertDialog(
        context: Context,
        title: String,
        msg: String,
        positiveBtnMsg: String,
        negativeBtnMsg: String? = null,
        positiveBtnCallback: (() -> Unit)? = null,
        negativeBtnCallback: (() -> Unit)? = null
    ) {
        if (isDialogShowing()) {
            return
        }

        dialogAlert = MaterialAlertDialogBuilder(context)
            .setTitle(title)
            .setMessage(msg)
            .setPositiveButton(positiveBtnMsg) { dialog, _ ->
                positiveBtnCallback?.invoke()
                dialog.dismiss()
            }
            .setNegativeButton(negativeBtnMsg) { dialog, _ ->
                negativeBtnCallback?.invoke()
                dialog.dismiss()
            }
            .setCancelable(false)
            .show()
    }
}
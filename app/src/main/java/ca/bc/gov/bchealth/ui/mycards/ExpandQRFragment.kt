package ca.bc.gov.bchealth.ui.mycards

import android.app.Dialog
import android.graphics.Bitmap
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidmads.library.qrgenearator.QRGContents
import androidmads.library.qrgenearator.QRGEncoder
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import ca.bc.gov.bchealth.R
import ca.bc.gov.bchealth.databinding.FragmentExpandQRBinding
import ca.bc.gov.bchealth.utils.viewBindings

/**
 * [ExpandQRFragment]
 *
 * @author amit metri
 */
class ExpandQRFragment : DialogFragment(R.layout.fragment_expand_q_r) {

    private val binding by viewBindings(FragmentExpandQRBinding::bind)

    private val args: ExpandQRFragmentArgs by navArgs()

    override fun onStart() {
        super.onStart()
        val dialog: Dialog? = dialog
        if (dialog != null) {
            val width = ViewGroup.LayoutParams.MATCH_PARENT
            val height = ViewGroup.LayoutParams.WRAP_CONTENT
            dialog.window?.setLayout(width, height)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.imgQrCode.setImageBitmap(getBarcode(args.qrData))

        binding.btnClose.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun getBarcode(data: String): Bitmap {
        val qrcode = QRGEncoder(data.removePrefix("shc:/"), null, QRGContents.Type.TEXT, 1200)
        return qrcode.encodeAsBitmap()
    }
}

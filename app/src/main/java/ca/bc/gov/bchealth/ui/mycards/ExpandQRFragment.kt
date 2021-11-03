package ca.bc.gov.bchealth.ui.mycards

import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import ca.bc.gov.bchealth.R
import ca.bc.gov.bchealth.databinding.FragmentExpandQRBinding
import ca.bc.gov.bchealth.utils.getBarcode
import ca.bc.gov.bchealth.utils.viewBindings
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking

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

        runBlocking {
            try {
                val bitmap = async {
                    args.qrData.getBarcode()
                }
                binding.imgQrCode.setImageBitmap(bitmap.await())
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        binding.btnClose.setOnClickListener {
            findNavController().popBackStack()
        }
    }
}

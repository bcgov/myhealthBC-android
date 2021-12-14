package ca.bc.gov.bchealth.ui.healthpass

import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import ca.bc.gov.bchealth.R
import ca.bc.gov.bchealth.databinding.FragmentExpandQRBinding
import ca.bc.gov.bchealth.utils.viewBindings
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

/**
 * [ExpandQRFragment]
 *
 * @author amit metri
 */
@AndroidEntryPoint
class ExpandQRFragment : DialogFragment(R.layout.fragment_expand_q_r) {

    private val binding by viewBindings(FragmentExpandQRBinding::bind)
    private val viewModel: QrCodeGeneratorViewModel by viewModels()
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

        binding.btnClose.setOnClickListener {
            findNavController().popBackStack()
        }

        viewLifecycleOwner.lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    if (state.qrCodeImage != null) {
                        binding.imgQrCode.setImageBitmap(state.qrCodeImage)
                    }
                }
            }
        }
        viewModel.generateQrCode(args.shcUri)
    }
}

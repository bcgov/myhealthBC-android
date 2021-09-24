package ca.bc.gov.bchealth.ui.addcard

import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import ca.bc.gov.bchealth.R
import ca.bc.gov.bchealth.databinding.FragmentAddCardOptionsBinding
import ca.bc.gov.bchealth.utils.viewBindings
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint


/**
 * [AddCardOptionFragment]
 *
 * @author Pinakin Kansara
 */
@AndroidEntryPoint
class AddCardOptionFragment : Fragment(R.layout.fragment_add_card_options) {

    private val binding by viewBindings(FragmentAddCardOptionsBinding::bind)

    private val viewModel: AddCardOptionViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnScanQrCode.setOnClickListener {
            findNavController().navigate(R.id.onBoardingFragment)
        }

        val action = registerForActivityResult(
            ActivityResultContracts.GetContent()
        ) {
            viewModel.processUploadedImage(it, requireContext(), object : UploadResultListener {
                override fun onSuccess() {
                    println("Successfully imported image!")
                    findNavController().popBackStack()
                }

                override fun onFailure() {
                    println("Invalid image!")
                    showError()
                }
            })
        }

        binding.btnImagePicker.setOnClickListener {
            action.launch("image/*")
        }

        binding.btnGetCard.setOnClickListener {
            // TODO: Start Add Card with citizen detail flow.
        }

        binding.toolbar.imgAction.setImageResource(R.drawable.ic_plus)
        binding.toolbar.imgAction.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun showError() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.bc_invalid_barcode_title))
            .setCancelable(false)
            .setMessage(getString(R.string.bc_invalid_barcode_upload_message))
            .setPositiveButton(getString(android.R.string.ok)) { dialog, which ->
                dialog.dismiss()
            }
            .show()
    }

    interface UploadResultListener {
        fun onSuccess()
        fun onFailure()
    }
}

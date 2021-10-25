package ca.bc.gov.bchealth.ui.addcard

import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import ca.bc.gov.bchealth.R
import ca.bc.gov.bchealth.analytics.AnalyticsAction
import ca.bc.gov.bchealth.analytics.AnalyticsText
import ca.bc.gov.bchealth.analytics.SelfDescribingEvent
import ca.bc.gov.bchealth.databinding.FragmentAddCardOptionsBinding
import ca.bc.gov.bchealth.utils.viewBindings
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.snowplowanalytics.snowplow.Snowplow
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
            findNavController().navigate(R.id.action_addCardOptionFragment_to_onBoardingFragment)
        }

        val action = registerForActivityResult(
            ActivityResultContracts.GetContent()
        ) {
            if (it != null)
                viewModel.processUploadedImage(it, requireContext())
        }

        viewModel.uploadStatus.observe(viewLifecycleOwner, {
            if (it) {
                //Snowplow event
                Snowplow.getDefaultTracker()?.track(
                    SelfDescribingEvent
                    .get(AnalyticsAction.AddQR, AnalyticsText.Upload))

                findNavController().popBackStack(R.id.myCardsFragment, false)
            } else {
                showError()
            }
        })

        binding.btnImagePicker.setOnClickListener {
            action.launch("image/*")
        }

        binding.btnGetCard.setOnClickListener {
            findNavController()
                .navigate(R.id.action_addCardOptionFragment_to_fetchVaccineCardFragment)
        }

        binding.toolbar.apply {
            ivBack.visibility = View.VISIBLE
            ivBack.setImageResource(R.drawable.ic_acion_back)
            tvTitle.visibility = View.VISIBLE
            tvTitle.text = getString(R.string.add_a_bc_vaccine_card)
            ivBack.setOnClickListener {
                findNavController().popBackStack()
            }
            line1.visibility = View.VISIBLE
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
}

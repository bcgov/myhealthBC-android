package ca.bc.gov.bchealth.ui.addcard

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import ca.bc.gov.bchealth.R
import ca.bc.gov.bchealth.databinding.FragmentAddCardOptionsBinding
import ca.bc.gov.bchealth.utils.viewBindings
import dagger.hilt.android.AndroidEntryPoint

/**
 * [AddCardOptionFragment]
 *
 * @author Pinakin Kansara
 */
@AndroidEntryPoint
class AddCardOptionFragment : Fragment(R.layout.fragment_add_card_options) {

    private val binding by viewBindings(FragmentAddCardOptionsBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnScanQrCode.setOnClickListener {
            findNavController().navigate(R.id.action_addCardOptionFragment_to_onBoardingFragment)
        }

        binding.btnImagePicker.setOnClickListener {
            // TODO:- Start Image Chooser Flow
        }

        binding.btnGetCard.setOnClickListener {
            // TODO: Start Add Card with citizen detail flow.
        }

        binding.toolbar.imgAction.setImageResource(R.drawable.ic_plus)
        binding.toolbar.imgAction.setOnClickListener {
            findNavController().popBackStack()
        }

    }
}

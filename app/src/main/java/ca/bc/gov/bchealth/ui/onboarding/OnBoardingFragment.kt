package ca.bc.gov.bchealth.ui.onboarding

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import ca.bc.gov.bchealth.R
import ca.bc.gov.bchealth.databinding.FragmentOnboardingBinding
import ca.bc.gov.bchealth.utils.viewBindings
import dagger.hilt.android.AndroidEntryPoint

/**
 * [OnBoardingFragment]
 *
 * @author amit metri
 */
@AndroidEntryPoint
class OnBoardingFragment : Fragment(R.layout.fragment_onboarding) {

    private val binding by viewBindings(FragmentOnboardingBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnAllowCameraPermission.setOnClickListener {
            // TODO:-
            // Request for Camera permission
            // On Permission granted open Barcode Scanner screen
            // Handle Permission related edge cases
        }

        binding.txtSkipForNow.setOnClickListener {
            findNavController().popBackStack()
        }
    }
}

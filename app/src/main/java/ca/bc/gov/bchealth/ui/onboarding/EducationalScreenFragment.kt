package ca.bc.gov.bchealth.ui.onboarding

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import ca.bc.gov.bchealth.R
import ca.bc.gov.bchealth.databinding.FragmentEducationalScreenBinding
import ca.bc.gov.bchealth.ui.onboarding.EducationalScreenAdapter.Companion.SLIDER_POSITION
import ca.bc.gov.bchealth.utils.viewBindings

class EducationalScreenFragment : Fragment(R.layout.fragment_educational_screen) {

    private val binding by viewBindings(FragmentEducationalScreenBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        arguments?.takeIf { it.containsKey(SLIDER_POSITION) }?.apply {
            when (get(SLIDER_POSITION)) {
                0 -> {
                    binding.txtSliderTitle.text = getString(R.string.health_passes)
                    binding.txtSliderDescription.text = getString(R.string.onboarding_message_1)
                }
                1 -> {
                    binding.txtSliderTitle.text = getString(R.string.health_resource)
                    binding.txtSliderDescription.text = getString(R.string.onboarding_message_2)
                }
                2 -> {
                    binding.txtSliderTitle.text = getString(R.string.news_feed)
                    binding.txtSliderDescription.text = getString(R.string.onboarding_message_3)
                }
            }
        }
    }
}

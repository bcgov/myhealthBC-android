package ca.bc.gov.bchealth.ui.onboarding

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import ca.bc.gov.bchealth.R
import ca.bc.gov.bchealth.databinding.FragmentEducationalScreenBinding
import ca.bc.gov.bchealth.utils.viewBindings

// fragment initialization parameter
private const val ARG_PARAM_POSITION = "param1"
private const val ARG_PARAM_RE_ON_BOARDING = "ARG_PARAM_RE_ON_BOARDING"

class EducationalScreenFragment : Fragment(R.layout.fragment_educational_screen) {

    private val binding by viewBindings(FragmentEducationalScreenBinding::bind)
    private var position: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            position = it.getInt(ARG_PARAM_POSITION)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (arguments?.getBoolean(ARG_PARAM_RE_ON_BOARDING) == true) {
            setIntroScreen(4)
        } else {
            position?.let {
                setIntroScreen(it)
            }
        }
    }

    private fun setIntroScreen(position: Int) {
        binding.apply {

            var title: String? = null
            var description: String? = null

            when (position) {
                0 -> {
                    title = getString(R.string.onboarding_health_records_title)
                    description = getString(R.string.onboarding_health_records_desc)
                    ivTopIcon.apply {
                        setImageResource(R.drawable.ic_onboarding_health_records)
                        visibility = View.VISIBLE
                    }
                }

                1 -> {
                    title = getString(R.string.onboarding_dependents_title)
                    description = getString(R.string.onboarding_dependents_desc)
                    ivTopRight.apply {
                        setImageResource(R.drawable.ic_onboarding_dependent)
                        visibility = View.VISIBLE
                    }
                }

                2 -> {
                    title = getString(R.string.onboarding_health_passes_title)
                    description = getString(R.string.onboarding_health_passes_desc)
                    ivRightIcon.apply {
                        setImageResource(R.drawable.ic_onboarding_health_passes)
                        visibility = View.VISIBLE
                    }
                }

                4 -> {
                    title = getString(R.string.onboarding_services_title)
                    description = getString(R.string.onboarding_services_desc)
                    ivRightIcon.apply {
                        setImageResource(R.drawable.ic_onboarding_services)
                        visibility = View.VISIBLE
                    }
                    if (arguments?.getBoolean(ARG_PARAM_RE_ON_BOARDING) == true) {
                        binding.tvNew.isVisible = true
                    }
                }

                3 -> {
                    title = getString(R.string.onboarding_health_resources_title)
                    description = getString(R.string.onboarding_health_resources_desc)
                    ivLeftIcon.apply {
                        setImageResource(R.drawable.ic_onboarding_health_recources)
                        visibility = View.VISIBLE
                    }
                }
            }

            tvSliderTitle.text = title
            tvSliderDescription.text = description
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(position: Int, isReOnBoardingRequired: Boolean) =
            EducationalScreenFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_PARAM_POSITION, position)
                    putBoolean(ARG_PARAM_RE_ON_BOARDING, isReOnBoardingRequired)
                }
            }
    }
}

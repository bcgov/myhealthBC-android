package ca.bc.gov.bchealth.ui.onboarding

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import ca.bc.gov.bchealth.R
import ca.bc.gov.bchealth.databinding.FragmentEducationalScreenBinding
import ca.bc.gov.bchealth.utils.viewBindings

// fragment initialization parameter
private const val ARG_PARAM_POSITION = "param1"
private const val ARG_PARAM_IS_DEPENDENT_ONLY = "ARG_PARAM_IS_DEPENDENT_ONLY"

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

        if (arguments?.getBoolean(ARG_PARAM_IS_DEPENDENT_ONLY) == true) {
            showDependentRecordsIntro()
        } else {
            when (position) {
                0 -> showHealthRecordsIntro()
                1 -> showDependentRecordsIntro()
                2 -> showHealthPassesIntro()
                3 -> showResourcesIntro()
            }
        }
    }

    private fun showResourcesIntro() {
        binding.apply {
            tvSliderTitle.text = getString(R.string.onboarding_health_resources_title)
            tvSliderDescription.text = getString(R.string.onboarding_health_resources_desc)
            ivLeftIcon.apply {
                setImageResource(R.drawable.ic_onboarding_health_recources)
                visibility = View.VISIBLE
            }
        }
    }

    private fun showDependentRecordsIntro() {
        binding.apply {
            tvSliderTitle.text = getString(R.string.onboarding_dependents_title)
            tvSliderDescription.text = getString(R.string.onboarding_dependents_desc)
            ivTopRight.apply {
                setImageResource(R.drawable.ic_onboarding_dependent)
                visibility = View.VISIBLE
            }
        }
    }

    private fun showHealthRecordsIntro() {
        binding.apply {
            tvSliderTitle.text = getString(R.string.onboarding_health_records_title)
            tvSliderDescription.text = getString(R.string.onboarding_health_records_desc)
            ivTopIcon.apply {
                setImageResource(R.drawable.ic_onboarding_health_records)
                visibility = View.VISIBLE
            }
        }
    }

    private fun showHealthPassesIntro() {
        binding.apply {
            tvSliderTitle.text = getString(R.string.onboarding_health_passes_title)
            tvSliderDescription.text = getString(R.string.onboarding_health_passes_desc)
            ivRightIcon.apply {
                setImageResource(R.drawable.ic_onboarding_health_passes)
                visibility = View.VISIBLE
            }
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(position: Int, isDependentOnly: Boolean) =
            EducationalScreenFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_PARAM_POSITION, position)
                    putBoolean(ARG_PARAM_IS_DEPENDENT_ONLY, isDependentOnly)
                }
            }
    }
}

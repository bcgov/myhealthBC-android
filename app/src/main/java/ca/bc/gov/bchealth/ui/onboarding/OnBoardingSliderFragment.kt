package ca.bc.gov.bchealth.ui.onboarding

import android.os.Bundle
import android.view.View
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import ca.bc.gov.bchealth.BuildConfig
import ca.bc.gov.bchealth.R
import ca.bc.gov.bchealth.databinding.FragmentOnboardingSliderBinding
import ca.bc.gov.bchealth.utils.toggleVisibility
import ca.bc.gov.bchealth.utils.viewBindings
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class OnBoardingSliderFragment : Fragment(R.layout.fragment_onboarding_slider) {

    private val binding by viewBindings(FragmentOnboardingSliderBinding::bind)
    private val viewModel: OnBoardingSliderViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requireActivity().onBackPressedDispatcher.addCallback(this) {
            requireActivity().finishAndRemoveTask()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val educationalScreenAdapter = EducationalScreenAdapter(this, viewModel.isReOnBoardingRequired)

        binding.viewpagerOnBoardingSlides.adapter = educationalScreenAdapter
        binding.tabOnBoardingSlides.toggleVisibility(viewModel.isReOnBoardingRequired.not())

        TabLayoutMediator(
            binding.tabOnBoardingSlides,
            binding.viewpagerOnBoardingSlides
        ) { _, _ -> }.attach()

        binding.viewpagerOnBoardingSlides.registerOnPageChangeCallback(object :
                ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    if (position == educationalScreenAdapter.itemCount - 1) {
                        val buttonText = if (viewModel.isReOnBoardingRequired) {
                            R.string.btn_ok
                        } else {
                            R.string.get_started
                        }
                        binding.btnNextSlide.text = getString(buttonText)
                        binding.tvSkip.visibility = View.INVISIBLE
                    } else {
                        binding.btnNextSlide.text = getString(R.string.next)
                        binding.tvSkip.visibility = View.VISIBLE
                    }
                }
            })

        binding.btnNextSlide.setOnClickListener {
            if (educationalScreenAdapter.itemCount == (getCurrentItem() + 1)) {
                navigateToHealthPasses()
            } else {
                binding.viewpagerOnBoardingSlides.currentItem = (getCurrentItem() + 1)
            }
        }

        binding.tvSkip.setOnClickListener {
            navigateToHealthPasses()
        }
    }

    private fun getCurrentItem(): Int {
        return binding.viewpagerOnBoardingSlides.currentItem
    }

    private fun navigateToHealthPasses() {
        viewModel.setOnBoardingRequired(false).invokeOnCompletion {
            viewModel.setAppVersionCode(BuildConfig.VERSION_CODE).invokeOnCompletion {
                findNavController().popBackStack()
            }
        }
    }
}

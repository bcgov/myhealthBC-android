package ca.bc.gov.bchealth.ui.onboarding

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.SavedStateHandle
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import ca.bc.gov.bchealth.BuildConfig
import ca.bc.gov.bchealth.R
import ca.bc.gov.bchealth.databinding.FragmentOnboardingSliderBinding
import ca.bc.gov.bchealth.utils.viewBindings
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class OnBoardingSliderFragment : Fragment(R.layout.fragment_onboarding_slider) {

    private val binding by viewBindings(FragmentOnboardingSliderBinding::bind)
    private val viewModel: OnBoardingSliderViewModel by viewModels()
    private lateinit var savedStateHandle: SavedStateHandle

    companion object {
        const val ON_BOARDING_SHOWN_SUCCESS = "ON_BOARDING_SHOWN_SUCCESS"
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        savedStateHandle = findNavController().previousBackStackEntry!!.savedStateHandle
        savedStateHandle.set(ON_BOARDING_SHOWN_SUCCESS, false)

        val educationalScreenAdapter = EducationalScreenAdapter(this)

        binding.viewpagerOnBoardingSlides.adapter = educationalScreenAdapter

        TabLayoutMediator(
            binding.tabOnBoardingSlides,
            binding.viewpagerOnBoardingSlides
        ) { _, _ -> }.attach()

        binding.viewpagerOnBoardingSlides.registerOnPageChangeCallback(object :
            ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                if (position == educationalScreenAdapter.itemCount - 1) {
                    binding.btnNextSlide.text = getString(R.string.get_started)
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
        savedStateHandle.set(ON_BOARDING_SHOWN_SUCCESS, true)
        viewModel.setOnBoardingRequired(false)
        viewModel.setAppVersionCode(BuildConfig.VERSION_CODE).invokeOnCompletion {
            findNavController().popBackStack()
        }
    }
}

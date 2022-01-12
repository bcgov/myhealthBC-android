package ca.bc.gov.bchealth.ui.onboarding

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import ca.bc.gov.bchealth.R
import ca.bc.gov.bchealth.databinding.FragmentOnboardingSliderBinding
import ca.bc.gov.bchealth.ui.login.LoginViewModel
import ca.bc.gov.bchealth.utils.viewBindings
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class OnBoardingSliderFragment : Fragment(R.layout.fragment_onboarding_slider) {

    private val binding by viewBindings(FragmentOnboardingSliderBinding::bind)

    private val viewModel: OnBoardingSliderViewModel by viewModels()

    private val loginViewModel: LoginViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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
                navigateToLoginFragment()
            } else {
                binding.viewpagerOnBoardingSlides.currentItem = (getCurrentItem() + 1)
            }
        }

        binding.tvSkip.setOnClickListener {
            navigateToLoginFragment()
        }
    }

    private fun getCurrentItem(): Int {
        return binding.viewpagerOnBoardingSlides.currentItem
    }

    private fun navigateToLoginFragment() {
        viewModel.setOnBoardingShown(true).invokeOnCompletion {
            viewModel.setNewFeatureShown(true).invokeOnCompletion {
                val navOptions = NavOptions.Builder()
                    .setPopUpTo(R.id.onBoardingSliderFragment, true)
                    .setEnterAnim(R.anim.nav_default_enter_anim)
                    .setExitAnim(R.anim.nav_default_exit_anim)
                    .setPopEnterAnim(R.anim.nav_default_pop_enter_anim)
                    .setPopExitAnim(R.anim.nav_default_pop_exit_anim)
                    .build()

                loginViewModel.checkLogin(
                    destinationId = R.id.myCardsFragment,
                    navOptions,
                    findNavController()
                )
            }
        }
    }
}

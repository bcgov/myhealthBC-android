package ca.bc.gov.bchealth.ui.onboarding

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import ca.bc.gov.bchealth.ui.onboarding.OnBoardingSliderFragment.Companion.NUMBER_OF_ON_BOARDING_SCREENS

/*
* Created by amit_metri on 12,October,2021
*/
class EducationalScreenAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int = NUMBER_OF_ON_BOARDING_SCREENS

    override fun createFragment(position: Int): Fragment {

        return when (position) {
            0 -> OnboardingHealthPassesFragment()
            1 -> OnboardingHealthResourcesFragment()
            2 -> OnboardingNewsFeedFragment()
            else -> OnboardingHealthPassesFragment()
        }
    }
}

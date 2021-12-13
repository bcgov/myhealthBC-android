package ca.bc.gov.bchealth.ui.onboarding

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

/*
* Created by amit_metri on 12,October,2021
*/
class EducationalScreenAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int = 4

    override fun createFragment(position: Int): Fragment {

        return when (position) {
            0 -> OnBoardingHealthPassesFragment()
            1 -> OnBoardingHealthRecordsFragment()
            2 -> OnBoardingHealthResourcesFragment()
            3 -> OnBoardingNewsFeedFragment()
            else -> OnBoardingHealthPassesFragment()
        }
    }
}

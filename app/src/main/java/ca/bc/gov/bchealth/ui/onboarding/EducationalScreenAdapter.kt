package ca.bc.gov.bchealth.ui.onboarding

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

/*
* Created by amit_metri on 12,October,2021
*/
class EducationalScreenAdapter(fragment: Fragment, private val isReOnBoardingRequired: Boolean) :
    FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int = if (isReOnBoardingRequired) 1 else 5

    override fun createFragment(position: Int): Fragment {
        return EducationalScreenFragment.newInstance(position, isReOnBoardingRequired)
    }
}

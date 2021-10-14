package ca.bc.gov.bchealth.ui.onboarding

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

/*
* Created by amit_metri on 12,October,2021
*/
class EducationalScreenAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int = 3

    override fun createFragment(position: Int): Fragment {

        val fragment = EducationalScreenFragment()

        fragment.arguments = Bundle().apply {
            putInt(SLIDER_POSITION, position)
        }

        return fragment
    }

    companion object {
        const val SLIDER_POSITION = "SLIDER_POSITION"
    }
}

package ca.bc.gov.bchealth.ui.cards

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import ca.bc.gov.bchealth.R
import ca.bc.gov.bchealth.databinding.FragmentCardsBinding
import ca.bc.gov.bchealth.databinding.FragmentOnboardingBinding
import ca.bc.gov.bchealth.ui.onboarding.OnBoardingFragment
import ca.bc.gov.bchealth.utils.viewBindings


/**
 * [CardsFragment]
 *
 * @author amit metri
 */
class CardsFragment : Fragment(R.layout.fragment_cards) {

    private val binding by viewBindings(FragmentCardsBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }
}
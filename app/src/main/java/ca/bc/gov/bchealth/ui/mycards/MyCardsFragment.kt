package ca.bc.gov.bchealth.ui.mycards

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import ca.bc.gov.bchealth.R
import ca.bc.gov.bchealth.databinding.FragmentMyCardsBinding
import ca.bc.gov.bchealth.utils.viewBindings
import dagger.hilt.android.AndroidEntryPoint

/**
 * [MyCardsFragment]
 *
 * @author Pinakin Kansara
 */
@AndroidEntryPoint
class MyCardsFragment : Fragment(R.layout.fragment_my_cards) {

    private val binding by viewBindings(FragmentMyCardsBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnAddCard.setOnClickListener {
            findNavController().navigate(R.id.action_myCardsFragment_to_addCardOptionFragment)
        }

        binding.recMyCards.emptyView = binding.emptyState
        // TODO:- Cards list either from db or dummy

        binding.toolbar.imgAction.setOnClickListener {
            findNavController().navigate(R.id.action_myCardsFragment_to_addCardOptionFragment)
        }
    }
}

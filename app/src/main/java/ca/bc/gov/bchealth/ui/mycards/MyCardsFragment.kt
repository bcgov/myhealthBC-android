package ca.bc.gov.bchealth.ui.mycards

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import ca.bc.gov.bchealth.R
import ca.bc.gov.bchealth.databinding.FragmentMyCardsBinding
import ca.bc.gov.bchealth.utils.viewBindings
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

/**
 * [MyCardsFragment]
 *
 * @author Pinakin Kansara
 */
@AndroidEntryPoint
class MyCardsFragment : Fragment(R.layout.fragment_my_cards) {

    private val viewModel: MyCardsViewModel by viewModels()

    private val binding by viewBindings(FragmentMyCardsBinding::bind)

    private lateinit var myCardsAdapter: MyCardsAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnAddCard.setOnClickListener {
            findNavController().navigate(R.id.action_myCardsFragment_to_addCardOptionFragment)
        }

        binding.toolbar.imgAction.setOnClickListener {
            findNavController().navigate(R.id.action_myCardsFragment_to_addCardOptionFragment)
        }

        setUpMyCardsAdapter()

        viewLifecycleOwner.lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.cards.collect { cards ->
                    if (cards == null) {
                        binding.progressBar.visibility = View.VISIBLE
                    } else {
                        binding.progressBar.visibility = View.INVISIBLE
                        if (cards.isEmpty()) {
                            binding.emptyState.visibility = View.VISIBLE
                        } else {
                            myCardsAdapter.cards = cards
                            myCardsAdapter.notifyDataSetChanged()
                        }
                    }
                }
            }
        }
    }

    private fun setUpMyCardsAdapter() {
        myCardsAdapter = MyCardsAdapter(emptyList())
        // binding.recMyCards.emptyView = binding.emptyState
        binding.recMyCards.adapter = myCardsAdapter
        binding.recMyCards.layoutManager = LinearLayoutManager(requireContext())
    }
}

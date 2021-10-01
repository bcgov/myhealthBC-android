package ca.bc.gov.bchealth.ui.mycards

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ca.bc.gov.bchealth.R
import ca.bc.gov.bchealth.databinding.FragmentMyCardsBinding
import ca.bc.gov.bchealth.utils.viewBindings
import dagger.hilt.android.AndroidEntryPoint
import java.util.Collections
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

    private var isManageCard: Boolean = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnAddCard.setOnClickListener {
            findNavController().navigate(R.id.action_myCardsFragment_to_addCardOptionFragment)
        }

        binding.toolbar.imgAction.contentDescription = getString(R.string.add_card)
        binding.toolbar.imgAction.setOnClickListener {
            findNavController().navigate(R.id.action_myCardsFragment_to_addCardOptionFragment)
        }
        setUpMyCardsAdapter()

        val callback = RecyclerDragCallBack(
            myCardsAdapter,
            ItemTouchHelper.UP.or(ItemTouchHelper.DOWN), 0
        )
        val helper = ItemTouchHelper(callback)

        binding.btnManageCards.setOnClickListener {
            if (!isManageCard) {
                binding.btnManageCards.text = getString(R.string.done)
                myCardsAdapter.canManage = true
                isManageCard = true
                helper.attachToRecyclerView(binding.recMyCards)
            } else {
                binding.btnManageCards.text = getString(R.string.manage_cards)
                myCardsAdapter.canManage = false
                isManageCard = false
                helper.attachToRecyclerView(null)
                viewModel.rearrange(myCardsAdapter.cards.toList())
            }
            // myCardsAdapter.notifyDataSetChanged()
            myCardsAdapter.notifyItemRangeChanged(0, myCardsAdapter.itemCount)
        }

        viewLifecycleOwner.lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.cards.collect { cards ->
                    if (cards == null) {
                        binding.progressBar.visibility = View.VISIBLE
                    } else {
                        binding.progressBar.visibility = View.GONE
                        if (cards.isNullOrEmpty()) {
                            binding.btnManageCards.visibility = View.GONE
                            binding.emptyState.visibility = View.VISIBLE
                        } else {
                            binding.btnManageCards.visibility = View.VISIBLE
                            binding.emptyState.visibility = View.GONE
                        }
                        myCardsAdapter.cards = cards.toMutableList()
                        myCardsAdapter.notifyDataSetChanged()
                    }
                }
            }
        }
    }

    private fun setUpMyCardsAdapter() {
        myCardsAdapter = MyCardsAdapter(mutableListOf()) { healthCard ->
            viewModel.unLink(healthCard.id, healthCard.uri)
        }

        binding.recMyCards.adapter = myCardsAdapter
        binding.recMyCards.layoutManager = LinearLayoutManager(requireContext())
    }

    inner class RecyclerDragCallBack(
        private val adapter: MyCardsAdapter,
        dragDirs: Int,
        swipeDirs: Int
    ) : ItemTouchHelper.SimpleCallback(dragDirs, swipeDirs) {

        override fun onMove(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            target: RecyclerView.ViewHolder
        ): Boolean {
            Collections.swap(
                myCardsAdapter.cards,
                viewHolder.adapterPosition,
                target.adapterPosition
            )
            adapter.notifyItemMoved(viewHolder.adapterPosition, target.adapterPosition)
            return false
        }

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        }
    }
}

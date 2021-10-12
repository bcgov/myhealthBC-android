package ca.bc.gov.bchealth.ui.mycards

import android.os.Bundle
import android.transition.Scene
import android.view.View
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ca.bc.gov.bchealth.R
import ca.bc.gov.bchealth.databinding.FragmentMyCardsBinding
import ca.bc.gov.bchealth.model.HealthCardDto
import ca.bc.gov.bchealth.utils.viewBindings
import com.google.android.material.dialog.MaterialAlertDialogBuilder
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

    private lateinit var sceneAddCard: Scene

    private lateinit var sceneMyCardsList: Scene

    private lateinit var sceneManageCards: Scene

    private lateinit var cardsListAdapter: MyCardsAdapter

    private lateinit var manageCardsAdapter: MyCardsAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewLifecycleOwner.lifecycleScope.launch {

            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {

                launch {
                    collectOnBoardingFlow()
                }
            }
        }
    }

    private fun healthPassesFlow() {
        sceneAddCard = Scene.getSceneForLayout(
            binding.sceneRoot,
            R.layout.scene_mycards_add_card,
            requireContext()
        )
        sceneMyCardsList = Scene.getSceneForLayout(
            binding.sceneRoot,
            R.layout.scene_mycards_cards_list,
            requireContext()
        )
        sceneManageCards =
            Scene.getSceneForLayout(
                binding.sceneRoot,
                R.layout.scene_mycards_manage_cards,
                requireContext()
            )

        binding.toolbar.apply {

            tvTitle.visibility = View.VISIBLE
            tvTitle.text = getString(R.string.bc_vaccine_cards)

            binding.toolbar.ivSettings.visibility = View.VISIBLE
            binding.toolbar.ivSettings.setImageResource(R.drawable.ic_add_card_blue)
            binding.toolbar.ivSettings.setOnClickListener {
                findNavController().navigate(R.id.action_myCardsFragment_to_addCardOptionFragment)
            }

            binding.toolbar.line1.visibility = View.INVISIBLE
        }

        val cardsTemp: MutableList<HealthCardDto> = mutableListOf()

        /*
        * Scenes are dependent on cards
        * */
        viewLifecycleOwner.lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.cards.collect { cards ->

                    cards?.toMutableList()?.let { it ->
                        val newCards = cards.filter { it.id !in cardsTemp.map { item -> item.id } }

                        cardsTemp.clear()
                        cardsTemp.addAll(cards)

                        if (newCards.isEmpty()) {
                            cards.forEach {
                                it.isExpanded = false
                            }
                            if (cards.size > 1)
                                cards[0].isExpanded = true
                        } else {
                            cards.forEach {
                                it.isExpanded = it.id == newCards[0].id
                            }
                        }
                    }

                    /*
                    * control scenes and loader
                    * */
                    if (cards == null) {
                        binding.progressBar.visibility = View.VISIBLE
                    } else {
                        binding.progressBar.visibility = View.GONE
                        if (cards.isEmpty()) {
                            /*
                            * Add card scene
                            * */
                            sceneAddCard.enter()
                            sceneAddCard.sceneRoot.findViewById<View>(R.id.btn_add_card)
                                .setOnClickListener {
                                    findNavController()
                                        .navigate(
                                            R.id.action_myCardsFragment_to_addCardOptionFragment
                                        )
                                }
                        } else {
                            enterCardsListScene(cards)
                        }
                    }
                }
            }
        }
    }

    /*
    * Cards List scene
    * */
    private fun enterCardsListScene(cards: List<HealthCardDto>) {
        sceneMyCardsList.enter()

        cardsListAdapter = MyCardsAdapter(cards.toMutableList()) { healthCard ->
            confirmUnlinking(healthCard = healthCard)
        }

        val recyclerViewCardsList =
            sceneMyCardsList.sceneRoot
                .findViewById<RecyclerView>(R.id.rec_cards_list)

        recyclerViewCardsList.adapter = cardsListAdapter

        recyclerViewCardsList.layoutManager =
            LinearLayoutManager(requireContext())

        val btnManageCards = sceneManageCards.sceneRoot.findViewById<Button>(R.id.btn_manage_cards)
        btnManageCards.text = getString(R.string.manage_cards)
        btnManageCards.setOnClickListener {
            enterManageCardsScene(cards)
        }

        cardsListAdapter.notifyItemRangeChanged(0, cardsListAdapter.itemCount)
    }

    /*
    * Manage Cards scene
    * */
    private fun enterManageCardsScene(cards: List<HealthCardDto>) {
        sceneManageCards.enter()

        manageCardsAdapter = MyCardsAdapter(cards.toMutableList(), true) { healthCard ->
            confirmUnlinking(healthCard = healthCard)
        }

        val recyclerViewManageCards =
            sceneManageCards.sceneRoot
                .findViewById<RecyclerView>(R.id.rec_manage_cards)

        recyclerViewManageCards.adapter = manageCardsAdapter

        recyclerViewManageCards.layoutManager =
            LinearLayoutManager(requireContext())

        val btnManageCards = sceneManageCards.sceneRoot.findViewById<Button>(R.id.btn_manage_cards)
        btnManageCards.text = getString(R.string.done)
        btnManageCards.setOnClickListener {
            viewModel.rearrange(manageCardsAdapter.cards.toList())
            enterCardsListScene(cards)
        }

        /*
        * Add cards movement functionality
        * */
        val callback = RecyclerDragCallBack(
            manageCardsAdapter,
            ItemTouchHelper.UP.or(ItemTouchHelper.DOWN), 0
        )
        val helper = ItemTouchHelper(callback)

        helper.attachToRecyclerView(recyclerViewManageCards)

        manageCardsAdapter.notifyItemRangeChanged(0, manageCardsAdapter.itemCount)
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
                manageCardsAdapter.cards,
                viewHolder.adapterPosition,
                target.adapterPosition
            )
            adapter.notifyItemMoved(viewHolder.adapterPosition, target.adapterPosition)
            return false
        }

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        }
    }

    private fun confirmUnlinking(healthCard: HealthCardDto) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.unlink_card))
            .setCancelable(false)
            .setMessage(getString(R.string.do_you_want_to_unlink))
            .setPositiveButton(getString(R.string.unlink)) { dialog, _ ->
                viewModel.unLink(healthCard.id, healthCard.uri)
                dialog.dismiss()
            }
            .setNegativeButton(getString(R.string.not_now)) { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private suspend fun collectOnBoardingFlow() {
        viewModel.isOnBoardingShown.collect { shown ->
            if (shown != null) {
                when (shown) {
                    true -> {
                        healthPassesFlow()
                    }

                    false -> {
                        val startDestination = findNavController().graph.startDestination
                        val navOptions = NavOptions.Builder()
                            .setPopUpTo(startDestination, true)
                            .build()
                        findNavController().navigate(
                            R.id.onBoardingSliderFragment,
                            null,
                            navOptions
                        )
                    }
                }
            }
        }
    }
}

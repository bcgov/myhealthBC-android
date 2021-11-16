package ca.bc.gov.bchealth.ui.mycards

import android.os.Bundle
import android.transition.Scene
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
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
import ca.bc.gov.bchealth.analytics.AnalyticsAction
import ca.bc.gov.bchealth.analytics.SelfDescribingEvent
import ca.bc.gov.bchealth.databinding.FragmentMyCardsBinding
import ca.bc.gov.bchealth.model.HealthCardDto
import ca.bc.gov.bchealth.utils.viewBindings
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.textview.MaterialTextView
import com.snowplowanalytics.snowplow.Snowplow
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

    private lateinit var sceneSingleCard: Scene

    private lateinit var sceneMyCardsList: Scene

    private lateinit var sceneManageCards: Scene

    private lateinit var cardsListAdapter: MyCardsAdapter

    private lateinit var manageCardsAdapter: MyCardsAdapter

    private var isEnterSceneMyCardsList: Boolean = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewLifecycleOwner.lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.CREATED) {
                launch {
                    setUpAnalyticsTracking()
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    collectOnBoardingFlow()
                }
            }
        }
    }

    private suspend fun setUpAnalyticsTracking() {
        viewModel.isAnalyticsEnabled.collect { isEnabled ->
            if (isEnabled != null) {
                when (isEnabled) {
                    true -> {
                        Snowplow.getDefaultTracker()?.resume()
                    }
                    false -> {
                        Snowplow.getDefaultTracker()?.pause()
                    }
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
        sceneSingleCard = Scene.getSceneForLayout(
            binding.sceneRoot,
            R.layout.scene_mycards_single_card,
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

        val cardsTemp: MutableList<HealthCardDto> = mutableListOf()

        /*
        * Scenes are dependent on cards
        * */
        viewLifecycleOwner.lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.cards.collect { cards ->

                    cards?.toMutableList()?.let { it ->
                        var newCards = cards.filter { it.id !in cardsTemp.map { item -> item.id } }

                        if (newCards.isEmpty()) {
                            newCards = cards.filter {
                                it.uri !in cardsTemp
                                    .map { item -> item.uri }
                            }
                        }

                        cardsTemp.clear()
                        cardsTemp.addAll(cards)

                        if (newCards.isEmpty()) {
                            cards.forEach {
                                it.isExpanded = false
                            }
                            if (cards.isNotEmpty())
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
                            enterAddCardScene()
                        } else {
                            // Show single card scene only after fresh app launch.
                            if (isEnterSceneMyCardsList) {
                                enterCardsListScene(cards)
                            } else {
                                enterSingleCardScene(cards)
                                isEnterSceneMyCardsList = true
                            }
                        }
                    }
                }
            }
        }
    }

    /*
    * Add card scene
    * */
    private fun enterAddCardScene() {

        sceneAddCard.enter()

        // Toolbar setup
        val toolBar = sceneAddCard.sceneRoot.findViewById<ViewGroup>(R.id.toolbar)
        val settingsButton = toolBar.findViewById<ImageView>(R.id.iv_settings)
        settingsButton.visibility = View.VISIBLE
        settingsButton.setOnClickListener {
            findNavController().navigate(R.id.action_myCardsFragment_to_settingFragment)
        }

        sceneAddCard.sceneRoot.findViewById<View>(R.id.btn_add_card)
            .setOnClickListener {
                findNavController()
                    .navigate(
                        R.id.action_myCardsFragment_to_addCardOptionFragment
                    )
            }
    }

    /*
    * Single card scene
    * */
    private fun enterSingleCardScene(cards: List<HealthCardDto>) {

        cards.forEach {
            it.isExpanded = false
        }
        if (cards.isNotEmpty())
            cards[0].isExpanded = true

        sceneSingleCard.enter()

        // Toolbar setup
        val toolBar = sceneSingleCard.sceneRoot.findViewById<ViewGroup>(R.id.toolbar)
        val settingsButton = toolBar.findViewById<ImageView>(R.id.iv_settings)
        settingsButton.visibility = View.VISIBLE
        settingsButton.setOnClickListener {
            findNavController().navigate(R.id.action_myCardsFragment_to_settingFragment)
        }

        // Recycler view setup
        cardsListAdapter = MyCardsAdapter(cards.toMutableList().subList(0, 1)) { healthCard ->
            confirmUnlinking(healthCard = healthCard)
        }
        val recyclerViewCardsList =
            sceneSingleCard.sceneRoot
                .findViewById<RecyclerView>(R.id.rec_cards_list)

        recyclerViewCardsList.adapter = cardsListAdapter

        recyclerViewCardsList.layoutManager =
            LinearLayoutManager(requireContext())

        // Other UI setup
        if (cards.size > 1) {
            sceneSingleCard.sceneRoot.findViewById<MaterialTextView>(R.id.tv_number_of_passes)
                .text = "${cards.size} passes"
            val btnViewAll = sceneSingleCard.sceneRoot.findViewById<Button>(R.id.btn_view_all)
            btnViewAll.visibility = View.VISIBLE
            btnViewAll.setOnClickListener {
                enterCardsListScene(cards)
            }
        } else {
            sceneSingleCard.sceneRoot.findViewById<MaterialTextView>(R.id.tv_number_of_passes)
                .visibility = View.GONE
        }

        sceneSingleCard.sceneRoot.findViewById<ShapeableImageView>(R.id.iv_add_card)
            .setOnClickListener {
                findNavController()
                    .navigate(
                        R.id.action_myCardsFragment_to_addCardOptionFragment
                    )
            }
    }

    /*
    * Cards List scene
    * */
    private fun enterCardsListScene(cards: List<HealthCardDto>) {

        sceneMyCardsList.enter()

        // Toolbar setup
        val toolBar = sceneMyCardsList.sceneRoot.findViewById<ViewGroup>(R.id.toolbar)
        val backButton = toolBar.findViewById<ImageView>(R.id.iv_back)
        backButton.visibility = View.VISIBLE
        backButton.setOnClickListener {
            enterSingleCardScene(cards)
        }
        val titleText = toolBar.findViewById<TextView>(R.id.tv_title)
        titleText.visibility = View.VISIBLE
        titleText.text = getString(R.string.add_bc_vaccine_card)
        val addButton = toolBar.findViewById<ImageView>(R.id.iv_settings)
        addButton.setImageResource(R.drawable.ic_plus)
        addButton.visibility = View.VISIBLE
        addButton.setOnClickListener {
            findNavController().navigate(R.id.action_myCardsFragment_to_addCardOptionFragment)
        }

        // Recycler view setup
        cardsListAdapter = MyCardsAdapter(cards.toMutableList()) { healthCard ->
            confirmUnlinking(healthCard = healthCard)
        }

        val recyclerViewCardsList =
            sceneMyCardsList.sceneRoot
                .findViewById<RecyclerView>(R.id.rec_cards_list)

        recyclerViewCardsList.adapter = cardsListAdapter

        recyclerViewCardsList.layoutManager =
            LinearLayoutManager(requireContext())

        cardsListAdapter.notifyItemRangeChanged(0, cardsListAdapter.itemCount)

        // Other UI setup
        val btnManageCards = sceneMyCardsList.sceneRoot.findViewById<Button>(R.id.btn_manage_cards)
        btnManageCards.text = getString(R.string.manage_cards)
        btnManageCards.setOnClickListener {
            enterManageCardsScene(cards)
        }
    }

    /*
    * Manage Cards scene
    * */
    private fun enterManageCardsScene(cards: List<HealthCardDto>) {

        sceneManageCards.enter()

        // Toolbar setup
        val toolBar = sceneManageCards.sceneRoot.findViewById<ViewGroup>(R.id.toolbar)
        val backButton = toolBar.findViewById<ImageView>(R.id.iv_back)
        backButton.visibility = View.VISIBLE
        backButton.setOnClickListener {
            enterCardsListScene(cards)
        }
        val titleText = toolBar.findViewById<TextView>(R.id.tv_title)
        titleText.visibility = View.VISIBLE
        titleText.text = getString(R.string.add_bc_vaccine_card)
        val addButton = toolBar.findViewById<ImageView>(R.id.iv_settings)
        addButton.setImageResource(R.drawable.ic_plus)
        addButton.visibility = View.VISIBLE
        addButton.setOnClickListener {
            findNavController().navigate(R.id.action_myCardsFragment_to_addCardOptionFragment)
        }

        // Recycler view setup
        manageCardsAdapter = MyCardsAdapter(cards.toMutableList(), true) { healthCard ->
            confirmUnlinking(healthCard = healthCard)
        }

        val recyclerViewManageCards =
            sceneManageCards.sceneRoot
                .findViewById<RecyclerView>(R.id.rec_manage_cards)

        recyclerViewManageCards.adapter = manageCardsAdapter

        recyclerViewManageCards.layoutManager =
            LinearLayoutManager(requireContext())

        // Other UI setup
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

                // Snowplow event
                Snowplow.getDefaultTracker()?.track(
                    SelfDescribingEvent
                        .get(AnalyticsAction.RemoveCard.value, null)
                )

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

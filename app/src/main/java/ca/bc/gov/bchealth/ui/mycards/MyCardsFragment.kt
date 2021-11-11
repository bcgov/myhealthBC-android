package ca.bc.gov.bchealth.ui.mycards

import android.content.Context
import android.content.Intent
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.net.Uri
import android.os.Bundle
import android.transition.Scene
import android.util.Base64
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
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
import ca.bc.gov.bchealth.ui.travelpass.TravelPassFragment
import ca.bc.gov.bchealth.utils.toast
import ca.bc.gov.bchealth.utils.viewBindings
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textview.MaterialTextView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.util.Collections

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

    private lateinit var currentScene: CurrentScene

    private lateinit var cardsListAdapter: MyCardsAdapter

    private lateinit var manageCardsAdapter: MyCardsAdapter

    private lateinit var cardsTemp: MutableList<HealthCardDto>

    private var newlyAddedCardPosition = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        cardsTemp = mutableListOf()
    }

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

        /*
        * Scenes are dependent on cards
        * */
        viewLifecycleOwner.lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.cards.collect { cards ->

                    /*
                    * Below logic is used to preserve expanded state of health card and
                    * to show newly added card in cards list.
                    * Temporary list is used to compare previous cards list to find
                    * newly added card.
                    *
                    * */
                    cards?.toMutableList()?.let { it ->

                        if (cardsTemp.isEmpty()) {
                            if (cards.isNotEmpty())
                                enterSingleCardScene(cards)
                            else
                                enterAddCardScene()
                            cardsTemp.clear()
                            cardsTemp.addAll(cards)
                            binding.progressBar.visibility = View.GONE
                            return@collect
                        }

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

                            var previouslyExpandedCard = 0

                            cards.forEach {
                                if(it.isExpanded){
                                    previouslyExpandedCard = cards.indexOf(it)
                                }
                            }

                            if (cards.isNotEmpty()) {
                                cards[previouslyExpandedCard].isExpanded = true
                            } else {
                                currentScene = CurrentScene.AddCardScene
                            }

                        } else {
                            cards.forEach {
                                if (it.id == newCards[0].id) {
                                    it.isExpanded = true
                                    newlyAddedCardPosition = cards.indexOf(it)
                                }
                                currentScene = CurrentScene.CardsListScene
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
                            when (currentScene) {
                                CurrentScene.AddCardScene -> enterAddCardScene()
                                CurrentScene.SingleCardScene -> enterSingleCardScene(cards)
                                CurrentScene.CardsListScene -> enterCardsListScene(cards)
                                CurrentScene.ManageCardsScene -> enterManageCardsScene(cards)
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

        currentScene = CurrentScene.AddCardScene

        // Toolbar setup
        val toolBar = sceneAddCard.sceneRoot.findViewById<ViewGroup>(R.id.toolbar)
        val settingsButton = toolBar.findViewById<ImageView>(R.id.iv_right_option)
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

        currentScene = CurrentScene.SingleCardScene

        // Toolbar setup
        val toolBar = sceneSingleCard.sceneRoot.findViewById<ViewGroup>(R.id.toolbar)
        val settingsButton = toolBar.findViewById<ImageView>(R.id.iv_right_option)
        settingsButton.visibility = View.VISIBLE
        settingsButton.setOnClickListener {
            findNavController().navigate(R.id.action_myCardsFragment_to_settingFragment)
        }

        // Recycler view setup
        cardsListAdapter = MyCardsAdapter(cards.toMutableList().subList(0, 1))

        cardsListAdapter.clickListener = {
            showFederalProof(it)
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

        /*
        * card swipe out functionality
        * */
        val callback = SwipeToDeleteCallBack(cards)
        val itemTouchHelper = ItemTouchHelper(callback)
        itemTouchHelper.attachToRecyclerView(recyclerViewCardsList)
    }

    inner class SwipeToDeleteCallBack(cards: List<HealthCardDto>) :
        ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {

        private val cardsTemp = cards.toMutableList()

        override fun onMove(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            target: RecyclerView.ViewHolder
        ): Boolean {
            return false
        }

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {

            var hardDelete = true
            val deletePass: HealthCardDto = cardsTemp[viewHolder.adapterPosition]
            val position = viewHolder.adapterPosition

            cardsListAdapter.cards.removeAt(position)
            cardsListAdapter.notifyItemRemoved(position)

            val snackBar = Snackbar.make(
                binding.constraintLayoutMyCards,
                getString(R.string.bc_vaccine_card_unlinked), Snackbar.LENGTH_LONG
            )
                .setAction(
                    getString(R.string.undo)
                ) {
                    hardDelete = false
                    cardsListAdapter.cards.add(position, deletePass)
                    cardsListAdapter.notifyItemInserted(position)
                }
            snackBar.addCallback(object : Snackbar.Callback() {
                override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
                    super.onDismissed(transientBottomBar, event)
                    if (hardDelete)
                        viewModel.unLink(deletePass.id, deletePass.uri)
                }
            })
            snackBar.show()
        }

        private val clearPaint = Paint().apply {
            xfermode =
                PorterDuffXfermode(PorterDuff.Mode.CLEAR)
        }

        override fun onChildDraw(
            c: Canvas,
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            dX: Float,
            dY: Float,
            actionState: Int,
            isCurrentlyActive: Boolean
        ) {
            val deleteIcon = ContextCompat.getDrawable(requireContext(), R.drawable.ic_un_link)
            val intrinsicWidth = deleteIcon?.intrinsicWidth
            val intrinsicHeight = deleteIcon?.intrinsicHeight
            val itemView = viewHolder.itemView
            val itemHeight = itemView.bottom - itemView.top
            val isCanceled = dX == 0f && !isCurrentlyActive

            if (isCanceled) {
                clearCanvas(
                    c,
                    itemView.right + dX,
                    itemView.top.toFloat(),
                    itemView.right.toFloat(),
                    itemView.bottom.toFloat()
                )
                super.onChildDraw(
                    c,
                    recyclerView,
                    viewHolder,
                    dX,
                    dY,
                    actionState,
                    isCurrentlyActive
                )
                return
            }

            // Calculate position of delete icon
            val deleteIconTop = itemView.top + (itemHeight - intrinsicHeight!!) / 2
            val deleteIconMargin = (itemHeight - intrinsicHeight) / 8
            val deleteIconLeft = itemView.right - deleteIconMargin - intrinsicWidth!!
            val deleteIconRight = itemView.right - deleteIconMargin
            val deleteIconBottom = deleteIconTop + intrinsicHeight

            // Draw the delete icon
            deleteIcon.setBounds(deleteIconLeft, deleteIconTop, deleteIconRight, deleteIconBottom)
            deleteIcon.draw(c)

            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
        }

        private fun clearCanvas(c: Canvas?, left: Float, top: Float, right: Float, bottom: Float) {
            c?.drawRect(left, top, right, bottom, clearPaint)
        }
    }

    /*
    * Cards List scene
    * */
    private fun enterCardsListScene(cards: List<HealthCardDto>) {

        sceneMyCardsList.enter()

        currentScene = CurrentScene.CardsListScene

        // Toolbar setup
        val toolBar = sceneMyCardsList.sceneRoot.findViewById<ViewGroup>(R.id.toolbar)
        val backButton = toolBar.findViewById<ImageView>(R.id.iv_left_option)
        backButton.visibility = View.VISIBLE
        backButton.setOnClickListener {
            enterSingleCardScene(cards)
        }
        val titleText = toolBar.findViewById<TextView>(R.id.tv_title)
        titleText.visibility = View.VISIBLE
        titleText.text = getString(R.string.bc_vaccine_cards)
        val addButton = toolBar.findViewById<ImageView>(R.id.iv_right_option)
        addButton.setImageResource(R.drawable.ic_plus)
        addButton.visibility = View.VISIBLE
        addButton.setOnClickListener {
            findNavController().navigate(R.id.action_myCardsFragment_to_addCardOptionFragment)
        }

        // Recycler view setup
        cardsListAdapter = MyCardsAdapter(cards.toMutableList())

        cardsListAdapter.clickListener = {
            showFederalProof(it)
        }

        val recyclerViewCardsList =
            sceneMyCardsList.sceneRoot
                .findViewById<RecyclerView>(R.id.rec_cards_list)

        recyclerViewCardsList.adapter = cardsListAdapter

        recyclerViewCardsList.layoutManager =
            LinearLayoutManager(requireContext())

        cardsListAdapter.notifyItemRangeChanged(0, cardsListAdapter.itemCount)

        viewLifecycleOwner.lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                delay(500)
                if (newlyAddedCardPosition > 0)
                    (recyclerViewCardsList.layoutManager as LinearLayoutManager)
                        .smoothScrollToPosition(
                            recyclerViewCardsList,
                            RecyclerView.State(), newlyAddedCardPosition
                        )
            }
        }

        // Other UI setup
        val btnManageCards = sceneMyCardsList.sceneRoot.findViewById<Button>(R.id.btn_manage_cards)
        btnManageCards.text = getString(R.string.manage_cards)
        btnManageCards.setOnClickListener {
            enterManageCardsScene(cards)
        }
    }

    private fun showFederalProof(healthCardDto: HealthCardDto) {

        if (!healthCardDto.federalPass.isNullOrEmpty()) {
            try {
                val decodedByteArray: ByteArray =
                    Base64.decode(healthCardDto.federalPass, Base64.DEFAULT)

                val filename = TravelPassFragment.tempFileName

                kotlin.runCatching {
                    requireContext().openFileOutput(filename, Context.MODE_PRIVATE).use {
                        it.write(decodedByteArray)
                    }
                }

                val internalStorageFiles = requireContext().filesDir

                internalStorageFiles?.listFiles()?.forEach { file ->

                    if (file.name == TravelPassFragment.tempFileName) {

                        try {
                            val authority =
                                requireActivity().applicationContext.packageName.toString() +
                                        ".fileprovider"
                            val uriToFile: Uri =
                                FileProvider.getUriForFile(requireActivity(), authority, file)

                            val shareIntent = Intent(Intent.ACTION_VIEW)
                            shareIntent.setDataAndType(uriToFile, "application/pdf")
                            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                            requireActivity().startActivity(shareIntent)

                        } catch (e: java.lang.Exception) {
                            e.printStackTrace()
                            fallBackToLocalPDFRenderer(healthCardDto)
                        }
                    }
                }
            } catch (e: Exception) {
                requireContext().toast(requireContext().getString(R.string.error_message))
            }
        }
    }

    private fun fallBackToLocalPDFRenderer(healthCardDto: HealthCardDto) {
        val action = MyCardsFragmentDirections
            .actionMyCardsFragmentToTravelPassFragment(healthCardDto)
        findNavController().navigate(action)
    }

    /*
    * Manage Cards scene
    * */
    private fun enterManageCardsScene(cards: List<HealthCardDto>) {

        sceneManageCards.enter()

        currentScene = CurrentScene.ManageCardsScene

        // Toolbar setup
        val toolBar = sceneManageCards.sceneRoot.findViewById<ViewGroup>(R.id.toolbar)
        val backButton = toolBar.findViewById<ImageView>(R.id.iv_left_option)
        backButton.visibility = View.VISIBLE
        backButton.setOnClickListener {
            enterCardsListScene(cards)
        }
        val titleText = toolBar.findViewById<TextView>(R.id.tv_title)
        titleText.visibility = View.VISIBLE
        titleText.text = getString(R.string.bc_vaccine_cards)
        val addButton = toolBar.findViewById<ImageView>(R.id.iv_right_option)
        addButton.setImageResource(R.drawable.ic_plus)
        addButton.visibility = View.VISIBLE
        addButton.setOnClickListener {
            findNavController().navigate(R.id.action_myCardsFragment_to_addCardOptionFragment)
        }

        // Recycler view setup
        manageCardsAdapter = MyCardsAdapter(cards.toMutableList(), true)

        manageCardsAdapter.clickListener = { healtCardDto ->
            confirmUnlinking(healtCardDto)
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

                        viewModel.isNewfeatureShown.collect { shown ->
                            if (shown != null) {
                                when (shown) {
                                    true -> {
                                        healthPassesFlow()
                                    }

                                    false -> {
                                        // TODO: 03/11/21 enable below flow when we plan to show new feature to existing users.
                                        // Also no need to disable once enabled.

                                        /*val startDestination =
                                            findNavController().graph.startDestination
                                        val navOptions = NavOptions.Builder()
                                            .setPopUpTo(startDestination, true)
                                            .build()
                                        findNavController().navigate(
                                            R.id.newFeatureFragment,
                                            null,
                                            navOptions
                                        )*/
                                        healthPassesFlow()
                                    }
                                }
                            }
                        }
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

    enum class CurrentScene {
        AddCardScene,
        SingleCardScene,
        CardsListScene,
        ManageCardsScene
    }
}

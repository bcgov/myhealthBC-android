package ca.bc.gov.bchealth.ui.healthpass

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.transition.Scene
import ca.bc.gov.bchealth.R
import ca.bc.gov.bchealth.databinding.FragmentMyCardsBinding
import ca.bc.gov.bchealth.utils.viewBindings
import ca.bc.gov.bchealth.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textview.MaterialTextView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

/**
 * @author Pinakin Kansara
 */
@AndroidEntryPoint
class HealthPassFragment : Fragment(R.layout.fragment_my_cards) {

    private val viewModel: HealthPassViewModel by viewModels()
    private val binding by viewBindings(FragmentMyCardsBinding::bind)
    private lateinit var healthPassAdapter: HealthPassAdapter
    private lateinit var sceneSingleHealthPass: Scene
    private lateinit var sceneNoCardPlaceHolder: Scene

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupToolBar()

        sceneSingleHealthPass = Scene.getSceneForLayout(
            binding.sceneRoot,
            R.layout.scene_mycards_single_card,
            requireContext()
        )

        sceneNoCardPlaceHolder = Scene.getSceneForLayout(
            binding.sceneRoot,
            R.layout.scene_mycards_add_card,
            requireContext()
        )

        healthPassAdapter = HealthPassAdapter(mutableListOf(),
            qrCodeClickListener = {
                val action =
                    HealthPassFragmentDirections.actionHealthPassFragmentToExpandQRFragment(it)
                findNavController().navigate(action)
            },
            federalPassClickListener = { patientId, federalPass ->
                if (federalPass.isNullOrBlank()) {
                    val action =
                        HealthPassFragmentDirections.actionHealthPassFragmentToFetchFederalTravelPass(
                            patientId
                        )
                    findNavController().navigate(action)
                } else {
                    val action =
                        HealthPassFragmentDirections.actionHealthPassFragmentToTravelPassFragment(
                            federalPass
                        )
                    findNavController().navigate(action)
                }

            })


        viewLifecycleOwner.lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.onBoardingRequired.collect {
                    if (it) {
                        findNavController().navigate(R.id.onBoardingSliderFragment)
                    } else {
                        viewLifecycleOwner.lifecycleScope.launch {
                            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                                collectHealthPasses()
                            }
                        }
                    }
                }
            }
        }
    }

    private suspend fun collectHealthPasses() {
        viewModel.healthPasses.collect { healthPasses ->
            if (::healthPassAdapter.isInitialized) {
                binding.progressBar.visibility = View.GONE
                if (!healthPasses.isNullOrEmpty()) {
                    showHealthPasses(healthPasses)
                } else {
                    showNoCardPlaceHolder()
                }
            }
        }
    }

    private fun setupToolBar() {
        binding.toolbar.ivRightOption.apply {
            visibility = View.VISIBLE
            setOnClickListener {
                findNavController().navigate(R.id.settingFragment)
            }
        }
    }

    private fun showHealthPasses(healthPasses: List<HealthPass>) {

        sceneSingleHealthPass.enter()

        setupRecyclerView(healthPasses)

        updateNumberOfPasses(healthPasses)

        initViewAllFunctionality(healthPasses)

        initAddHealthPass()
    }

    private fun initAddHealthPass() {
        val btnAddHealthPass: ShapeableImageView =
            sceneSingleHealthPass.sceneRoot.findViewById(R.id.iv_add_card)
        btnAddHealthPass.setOnClickListener {
            findNavController().navigate(R.id.addCardOptionFragment)
        }
    }

    private fun initViewAllFunctionality(healthPasses: List<HealthPass>) {
        val btnViewAll: MaterialButton =
            sceneSingleHealthPass.sceneRoot.findViewById(R.id.btn_view_all)
        btnViewAll.setOnClickListener {
            navigateToViewAllHealthPasses()
        }
        btnViewAll.visibility = if (healthPasses.size > 1) View.VISIBLE else View.GONE
    }

    private fun updateNumberOfPasses(healthPasses: List<HealthPass>) {
        val txtNumberOfPasses: MaterialTextView =
            sceneSingleHealthPass.sceneRoot.findViewById(R.id.tv_number_of_passes)

        if (healthPasses.size > 1) {
            val numberOfPasses = "${healthPasses.size} passes"
            txtNumberOfPasses.text = numberOfPasses
        } else {
            txtNumberOfPasses.visibility = View.GONE
        }
    }

    private fun setupRecyclerView(healthPasses: List<HealthPass>) {
        if (::healthPassAdapter.isInitialized) {
            val passes = healthPasses.toMutableList().subList(0, 1)
            passes.first().isExpanded = true
            healthPassAdapter.healthPasses = passes
            healthPassAdapter.notifyDataSetChanged()
        }
        val recHealthPasses: RecyclerView =
            sceneSingleHealthPass.sceneRoot.findViewById(R.id.rec_cards_list)
        recHealthPasses.adapter = healthPassAdapter
        recHealthPasses.layoutManager = LinearLayoutManager(requireContext())

        /*
        * card swipe out functionality
        * */
        val callback = SwipeToDeleteCallBack(healthPasses)
        val itemTouchHelper = ItemTouchHelper(callback)
        itemTouchHelper.attachToRecyclerView(recHealthPasses)
    }

    private fun showNoCardPlaceHolder() {
        sceneNoCardPlaceHolder.enter()
        val btnAddCardOptions: MaterialButton =
            sceneNoCardPlaceHolder.sceneRoot.findViewById(R.id.btn_add_card)
        btnAddCardOptions.setOnClickListener {
            findNavController().navigate(R.id.addCardOptionFragment)
        }
    }

    private fun navigateToViewAllHealthPasses() {
        findNavController().navigate(R.id.action_healthPassFragment_to_healthPassesFragment)
    }

    inner class SwipeToDeleteCallBack(healthPasses: List<HealthPass>) :
        ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {

        private val cardsTemp = healthPasses.toMutableList()

        override fun onMove(
            recyclerView: androidx.recyclerview.widget.RecyclerView,
            viewHolder: androidx.recyclerview.widget.RecyclerView.ViewHolder,
            target: androidx.recyclerview.widget.RecyclerView.ViewHolder
        ): Boolean {
            return false
        }

        override fun onSwiped(
            viewHolder: androidx.recyclerview.widget.RecyclerView.ViewHolder,
            direction: Int
        ) {

            var hardDelete = true
            val deletePass: HealthPass = cardsTemp[viewHolder.adapterPosition]
            val position = viewHolder.adapterPosition

            healthPassAdapter.healthPasses.removeAt(position)
            healthPassAdapter.notifyItemRemoved(position)

            val snackBar = Snackbar.make(
                binding.constraintLayoutMyCards,
                getString(R.string.bc_vaccine_card_unlinked), Snackbar.LENGTH_LONG
            )
                .setAction(
                    getString(R.string.undo)
                ) {
                    hardDelete = false
                    healthPassAdapter.healthPasses.add(position, deletePass)
                    healthPassAdapter.notifyItemInserted(position)
                }
            snackBar.addCallback(object : Snackbar.Callback() {
                override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
                    super.onDismissed(transientBottomBar, event)
                    if (hardDelete) {
                        viewModel.deleteHealthPass(deletePass.vaccineRecordId)
                    }
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
            recyclerView: androidx.recyclerview.widget.RecyclerView,
            viewHolder: androidx.recyclerview.widget.RecyclerView.ViewHolder,
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
}
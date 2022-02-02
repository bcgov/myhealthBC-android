package ca.bc.gov.bchealth.ui.healthpass

import android.content.Intent
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.transition.Scene
import ca.bc.gov.bchealth.R
import ca.bc.gov.bchealth.databinding.FragmentMyCardsBinding
import ca.bc.gov.bchealth.ui.auth.BioMetricState
import ca.bc.gov.bchealth.ui.auth.BiometricsAuthenticationFragment
import ca.bc.gov.bchealth.utils.viewBindings
import ca.bc.gov.bchealth.viewmodel.FederalTravelPassDecoderVideModel
import ca.bc.gov.bchealth.viewmodel.SharedViewModel
import ca.bc.gov.bchealth.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textview.MaterialTextView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.io.File

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
    private val sharedViewModel: SharedViewModel by activityViewModels()
    private val federalTravelPassDecoderVideModel: FederalTravelPassDecoderVideModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<BioMetricState>(
            BiometricsAuthenticationFragment.BIOMETRIC_STATE
        )?.observe(viewLifecycleOwner) {
            when (it) {
                BioMetricState.SUCCESS -> {
                    viewModel.onAuthenticationRequired(false)
                }
                else -> {
                    findNavController().popBackStack()
                }
            }
        }

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

        healthPassAdapter = HealthPassAdapter(
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
                    federalTravelPassDecoderVideModel.base64ToPDFFile(federalPass)
                }
            },
            itemClickListener = { healthPass ->
                healthPassAdapter.currentList.forEachIndexed { index, pass ->
                    if (healthPass.patientId == pass.patientId) {
                        pass.isExpanded = true
                        healthPassAdapter.notifyItemChanged(index)
                    } else {
                        pass.isExpanded = false
                        healthPassAdapter.notifyItemChanged(index)
                    }
                }
            }
        )

        sharedViewModel.modifiedRecordId.observe(
            viewLifecycleOwner,
            Observer {
                if (it > 0) {
                    findNavController().navigate(R.id.action_healthPassFragment_to_healthPassesFragment)
                }
            }
        )

        viewModel.launchCheck()

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    onBoardingFlow()
                }
                launch {
                    collectHealthPasses()
                }
                launch {
                    collectUiState()
                }
            }
        }
    }

    private suspend fun onBoardingFlow() {
        viewModel.uiState.collect { uiState ->
            if (uiState.isOnBoardingRequired) {
                findNavController().navigate(R.id.onBoardingSliderFragment)
                viewModel.onBoardingShown()
            }

            if (uiState.isAuthenticationRequired) {
                findNavController().navigate(R.id.biometricsAuthenticationFragment)
            }
        }
    }

    private suspend fun collectUiState() {
        federalTravelPassDecoderVideModel.uiState.collect { uiState ->
            if (uiState.travelPass != null) {
                val (federalTravelPass, file) = uiState.travelPass
                if (file != null) {
                    try {
                        showPDF(file)
                    } catch (e: Exception) {
                        navigateToViewTravelPass(federalTravelPass)
                    }
                } else {
                    navigateToViewTravelPass(federalTravelPass)
                }
                federalTravelPassDecoderVideModel.federalTravelPassShown()
            }
        }
    }

    private fun showPDF(file: File) {
        val authority =
            requireActivity().applicationContext.packageName.toString() +
                ".fileprovider"
        val uriToFile: Uri =
            FileProvider.getUriForFile(requireActivity(), authority, file)
        val shareIntent = Intent(Intent.ACTION_VIEW)
        shareIntent.setDataAndType(uriToFile, "application/pdf")
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        requireActivity().startActivity(shareIntent)
    }

    private fun navigateToViewTravelPass(federalTravelPass: String) {
        val action =
            HealthPassFragmentDirections.actionHealthPassFragmentToTravelPassFragment(
                federalTravelPass
            )
        findNavController().navigate(action)
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
            healthPassAdapter.submitList(passes)
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
            val position = viewHolder.bindingAdapterPosition
            val list = healthPassAdapter.currentList.toMutableList()
            val deletePass: HealthPass = list[position]
            list.removeAt(position)
            healthPassAdapter.submitList(list)
            healthPassAdapter.notifyItemRemoved(position)

            val snackBar = Snackbar.make(
                binding.constraintLayoutMyCards,
                getString(R.string.bc_vaccine_card_unlinked), Snackbar.LENGTH_LONG
            )
                .setAction(
                    getString(R.string.undo)
                ) {
                    hardDelete = false
                    val currentList = healthPassAdapter.currentList.toMutableList()
                    currentList.add(position, deletePass)
                    healthPassAdapter.submitList(currentList)
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
            deleteIcon.setBounds(
                deleteIconLeft,
                deleteIconTop,
                deleteIconRight,
                deleteIconBottom
            )
            deleteIcon.draw(c)

            super.onChildDraw(
                c,
                recyclerView,
                viewHolder,
                dX,
                dY,
                actionState,
                isCurrentlyActive
            )
        }

        private fun clearCanvas(
            c: Canvas?,
            left: Float,
            top: Float,
            right: Float,
            bottom: Float
        ) {
            c?.drawRect(left, top, right, bottom, clearPaint)
        }
    }
}

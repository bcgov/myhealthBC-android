package ca.bc.gov.bchealth.ui.healthpass

import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.os.bundleOf
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.recyclerview.widget.LinearLayoutManager
import ca.bc.gov.bchealth.R
import ca.bc.gov.bchealth.databinding.FragmentHelathPassesBinding
import ca.bc.gov.bchealth.ui.BaseFragment
import ca.bc.gov.bchealth.utils.PdfHelper
import ca.bc.gov.bchealth.utils.launchOnStart
import ca.bc.gov.bchealth.utils.viewBindings
import ca.bc.gov.bchealth.viewmodel.PdfDecoderViewModel
import ca.bc.gov.bchealth.viewmodel.SharedViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.io.File

/**
 * @author Pinakin Kansara
 */
@AndroidEntryPoint
class HealthPassesFragment : BaseFragment(R.layout.fragment_helath_passes) {

    private val viewModel: HealthPassViewModel by viewModels()
    private val binding by viewBindings(FragmentHelathPassesBinding::bind)
    private lateinit var healthPassAdapter: HealthPassAdapter
    private val sharedViewModel: SharedViewModel by activityViewModels()
    private var patientId: Long = -1L
    private val pdfDecoderViewModel: PdfDecoderViewModel by viewModels()
    private var fileInMemory: File? = null
    private var resultListener = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { _ ->
        fileInMemory?.delete()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        healthPassAdapter = HealthPassAdapter(
            qrCodeClickListener = {
                val action =
                    HealthPassesFragmentDirections.actionHealthPassesFragmentToExpandQRFragment(it)
                findNavController().navigate(action)
            },
            federalPassClickListener = { patientId, federalPass ->
                if (federalPass.isNullOrBlank()) {
                    val action =
                        HealthPassesFragmentDirections.actionHealthPassesFragmentToFetchFederalTravelPass(
                            patientId
                        )
                    findNavController().navigate(action)
                } else {
                    pdfDecoderViewModel.base64ToPDFFile(federalPass)
                }
            },
            itemClickListener = { healthPass ->
                healthPassAdapter.currentList.forEachIndexed { index, pass ->
                    if (healthPass.patientId == pass.patientId) {
                        pass.isExpanded = true
                        patientId = pass.patientId
                        healthPassAdapter.notifyItemChanged(index)
                    } else {
                        pass.isExpanded = false
                        healthPassAdapter.notifyItemChanged(index)
                    }
                }
            }
        )
        binding.recCardsList.adapter = healthPassAdapter
        binding.recCardsList.layoutManager =
            LinearLayoutManager(requireContext())

        sharedViewModel.modifiedRecordId.observe(
            viewLifecycleOwner,
            Observer {
                if (it > 0) {
                    patientId = it
                }
            }
        )

        launchOnStart {
            launch { collectHealthPasses() }
            launch { collectUiState() }
        }
    }

    override fun setToolBar(appBarConfiguration: AppBarConfiguration) {
        with(binding.layoutToolbar.topAppBar) {
            setNavigationIcon(R.drawable.ic_toolbar_back)
            setNavigationOnClickListener { findNavController().popBackStack() }
            title = getString(R.string.bc_vaccine_passes)
            inflateMenu(R.menu.menu_health_pass)
            setOnMenuItemClickListener { menu ->
                when (menu.itemId) {
                    R.id.menu_edit -> {
                        findNavController().navigate(R.id.action_healthPassesFragment_to_manageHealthPassFragment)
                    }
                }
                return@setOnMenuItemClickListener true
            }
        }
    }

    private fun navigateToViewTravelPass(federalTravelPass: String) {
        findNavController().navigate(
            R.id.pdfRendererFragment,
            bundleOf(
                "base64pdf" to federalTravelPass,
                "title" to getString(R.string.travel_pass)
            )
        )
    }

    private suspend fun collectHealthPasses() {
        viewModel.healthPasses.collect { healthPasses ->
            if (::healthPassAdapter.isInitialized) {
                var position = 0
                val passes = healthPasses.toMutableList()
                if (patientId == -1L) {
                    passes.first().isExpanded = true
                } else {
                    passes.forEachIndexed { index, healthPass ->
                        if (healthPass.patientId == patientId) {
                            healthPass.isExpanded = true
                            position = index
                        }
                    }
                }
                sharedViewModel.setModifiedRecordId(-1L)
                healthPassAdapter.submitList(passes)
                binding.recCardsList.layoutManager?.scrollToPosition(position)
            }
        }
    }

    private suspend fun collectUiState() {
        pdfDecoderViewModel.uiState.collect { uiState ->
            if (uiState.pdf != null) {
                val (federalTravelPass, file) = uiState.pdf
                if (file != null) {
                    try {
                        fileInMemory = file
                        PdfHelper().showPDF(file, requireActivity(), resultListener)
                    } catch (e: Exception) {
                        navigateToViewTravelPass(federalTravelPass)
                    }
                } else {
                    navigateToViewTravelPass(federalTravelPass)
                }
                pdfDecoderViewModel.resetUiState()
            }
        }
    }
}

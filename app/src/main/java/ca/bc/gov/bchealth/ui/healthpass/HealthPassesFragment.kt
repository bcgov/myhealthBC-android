package ca.bc.gov.bchealth.ui.healthpass

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import ca.bc.gov.bchealth.R
import ca.bc.gov.bchealth.databinding.SceneMycardsCardsListBinding
import ca.bc.gov.bchealth.utils.viewBindings
import ca.bc.gov.bchealth.viewmodel.FederalTravelPassDecoderVideModel
import ca.bc.gov.bchealth.viewmodel.SharedViewModel
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.imageview.ShapeableImageView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.io.File

/**
 * @author Pinakin Kansara
 */
@AndroidEntryPoint
class HealthPassesFragment : Fragment(R.layout.scene_mycards_cards_list) {

    private val viewModel: HealthPassViewModel by viewModels()
    private val binding by viewBindings(SceneMycardsCardsListBinding::bind)
    private lateinit var healthPassAdapter: HealthPassAdapter
    private val sharedViewModel: SharedViewModel by activityViewModels()
    private var patientId: Long = -1L
    private val federalTravelPassDecoderVideModel: FederalTravelPassDecoderVideModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Toolbar setup
        val toolBar: MaterialToolbar = view.findViewById(R.id.toolbar)
        val backButton: ShapeableImageView = toolBar.findViewById(R.id.iv_left_option)
        backButton.visibility = View.VISIBLE
        backButton.setOnClickListener {
            findNavController().popBackStack()
        }
        val titleText = toolBar.findViewById<TextView>(R.id.tv_title)
        titleText.visibility = View.VISIBLE
        titleText.text = getString(R.string.bc_vaccine_passes)
        val tvEdit = toolBar.findViewById<TextView>(R.id.tv_right_option)
        tvEdit.text = getString(R.string.edit)
        tvEdit.visibility = View.VISIBLE
        tvEdit.setOnClickListener {
            findNavController().navigate(R.id.action_healthPassesFragment_to_manageHealthPassFragment)
        }

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
                    federalTravelPassDecoderVideModel.base64ToPDFFile(federalPass)
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

        viewLifecycleOwner.lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                collectHealthPasses()
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
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
}

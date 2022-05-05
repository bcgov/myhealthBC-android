package ca.bc.gov.bchealth.ui.healthrecord.protectiveword

import android.os.Bundle
import android.text.InputFilter
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import ca.bc.gov.bchealth.R
import ca.bc.gov.bchealth.databinding.FragmentProtectiveWordBinding
import ca.bc.gov.bchealth.utils.AlertDialogHelper
import ca.bc.gov.bchealth.utils.makeLinks
import ca.bc.gov.bchealth.utils.redirect
import ca.bc.gov.bchealth.utils.show
import ca.bc.gov.bchealth.utils.viewBindings
import ca.bc.gov.bchealth.viewmodel.SharedViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


@AndroidEntryPoint
class ProtectiveWordFragment : Fragment(R.layout.fragment_protective_word) {

    private val binding by viewBindings(FragmentProtectiveWordBinding::bind)
    private val viewModel: ProtectiveWordViewModel by viewModels()
    private val args: ProtectiveWordFragmentArgs by navArgs()
    private val sharedViewModel: SharedViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpToolbar()

        binding.tvDesc.makeLinks(
            Pair(
                getString(R.string.more),
                View.OnClickListener {
                    requireContext().redirect("https://www2.gov.bc.ca/gov/content/health/health-drug-coverage/pharmacare-for-bc-residents/pharmanet/protective-word-for-a-pharmanet-record")
                }
            )
        )

        binding.etProtectiveWord.apply {
            doAfterTextChanged {
                it?.let {
                    binding.btnContinue.isEnabled = it.count() > 0
                    binding.tlProtectiveWord.error = null
                }
            }
            filters += InputFilter.AllCaps()
        }

        binding.btnContinue.setOnClickListener {
            viewModel.fetchMedicationRecords(
                args.patientId,
                binding.etProtectiveWord.text.toString()
            )
        }
        binding.btnCancel.setOnClickListener {
            findNavController().popBackStack()
        }

        observeMedicationRecords()
    }

    private fun observeMedicationRecords() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { fetchMedicationUiState ->

                    binding.progressBar.isVisible = fetchMedicationUiState.onLoading
                    binding.btnContinue.isEnabled = fetchMedicationUiState.isButtonClickEnabled

                    if (fetchMedicationUiState.errorData != null) {
                        viewModel.resetUiState()
                        AlertDialogHelper.showAlertDialog(
                            context = requireContext(),
                            title = getString(fetchMedicationUiState.errorData.title),
                            msg = getString(fetchMedicationUiState.errorData.message),
                            positiveBtnMsg = getString(R.string.btn_ok)
                        )
                    }

                    if (fetchMedicationUiState.wrongProtectiveWord) {
                        viewModel.resetUiState()
                        binding.tlProtectiveWord.error = getString(R.string.wrong_protective_word)
                    }

                    if (fetchMedicationUiState.isRecordsUpdated) {
                        sharedViewModel.isProtectiveWordAdded = true
                        // parentFragmentManager.setFragmentResult(
                        //     KEY_MEDICATION_RECORD_REQUEST,
                        //     bundleOf(
                        //         KEY_MEDICATION_RECORD_UPDATED to true,
                        //     )
                        // )
                        findNavController().popBackStack()
                    }
                }
            }
        }
    }

    private fun setUpToolbar() {
        binding.toolbar.apply {
            tvTitle.show()
            tvTitle.text = getString(R.string.unlock_records)
            ivLeftOption.apply {
                this.show()
                setImageResource(R.drawable.ic_scanner_close)
                setColorFilter(ContextCompat.getColor(context, R.color.primary_blue))
                setOnClickListener {
                    findNavController().popBackStack()
                }
            }
            line1.visibility = View.VISIBLE
        }
    }
}

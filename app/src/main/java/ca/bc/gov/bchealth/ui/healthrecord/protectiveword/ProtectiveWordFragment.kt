package ca.bc.gov.bchealth.ui.healthrecord.protectiveword

import android.os.Bundle
import android.text.InputFilter
import android.view.View
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.ui.AppBarConfiguration
import ca.bc.gov.bchealth.R
import ca.bc.gov.bchealth.databinding.FragmentProtectiveWordBinding
import ca.bc.gov.bchealth.ui.BaseFragment
import ca.bc.gov.bchealth.utils.AlertDialogHelper
import ca.bc.gov.bchealth.utils.hideKeyboard
import ca.bc.gov.bchealth.utils.makeLinks
import ca.bc.gov.bchealth.utils.redirect
import ca.bc.gov.bchealth.utils.viewBindings
import ca.bc.gov.bchealth.viewmodel.SharedViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

const val KEY_MEDICATION_RECORD_REQUEST = "KEY_MEDICATION_RECORD_REQUEST"
const val KEY_MEDICATION_RECORD_UPDATED = "KEY_MEDICATION_RECORD_UPDATED"

@AndroidEntryPoint
class ProtectiveWordFragment : BaseFragment(R.layout.fragment_protective_word) {

    private val binding by viewBindings(FragmentProtectiveWordBinding::bind)
    private val viewModel: ProtectiveWordViewModel by viewModels()
    private val args: ProtectiveWordFragmentArgs by navArgs()
    private val sharedViewModel: SharedViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
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
            requireContext().hideKeyboard(it)
            binding.scrollView.clearFocus()
            viewModel.fetchMedicationRecords(
                args.patientId,
                binding.etProtectiveWord.text.toString()
            )
        }
        binding.btnCancel.setOnClickListener {
            findNavController().popBackStack()
        }

        observeMedicationRecords()

        binding.scrollView.setOnTouchListener { view, _ ->
            requireContext().hideKeyboard(view)
            view?.clearFocus()
            return@setOnTouchListener true
        }
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
                        parentFragmentManager.setFragmentResult(
                            KEY_MEDICATION_RECORD_REQUEST,
                            bundleOf(
                                KEY_MEDICATION_RECORD_UPDATED to true,
                            )
                        )
                        findNavController().popBackStack()
                    }
                }
            }
        }
    }

    override fun setToolBar(appBarConfiguration: AppBarConfiguration) {
        with(binding.layoutToolbar.topAppBar) {
            setNavigationIcon(R.drawable.ic_toolbar_back)
            setNavigationOnClickListener {
                findNavController().popBackStack()
            }
            title = getString(R.string.unlock_records)
        }
    }
}

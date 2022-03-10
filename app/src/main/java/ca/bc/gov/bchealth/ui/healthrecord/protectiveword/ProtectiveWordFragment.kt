package ca.bc.gov.bchealth.ui.healthrecord.protectiveword

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import ca.bc.gov.bchealth.R
import ca.bc.gov.bchealth.databinding.FragmentProtectiveWordBinding
import ca.bc.gov.bchealth.utils.AlertDialogHelper
import ca.bc.gov.bchealth.utils.show
import ca.bc.gov.bchealth.utils.viewBindings
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ProtectiveWordFragment : Fragment(R.layout.fragment_protective_word) {

    private val binding by viewBindings(FragmentProtectiveWordBinding::bind)
    private val viewModel: ProtectiveWordViewModel by viewModels()
    private val args: ProtectiveWordFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpToolbar()

        binding.btnContinue.setOnClickListener {
            viewModel.fetchMedicationRecords(args.patientId, binding.etProtectiveWord.text.toString())
            // if(viewModel.isProtectiveWordValid(binding.etProtectiveWord.text.toString())) {
            //     viewModel.saveProtectiveWord(binding.etProtectiveWord.text.toString())
            //     // viewModel.clearIsProtectiveWordRequired()
            //     findNavController().popBackStack()
            // } else {
            //     binding.etProtectiveWord.error = "Invalid protective word. Try again."
            // }
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

                    showLoader(fetchMedicationUiState.onLoading)

                    if (fetchMedicationUiState.errorData != null) {
                        AlertDialogHelper.showAlertDialog(
                            context = requireContext(),
                            title = getString(fetchMedicationUiState.errorData.title),
                            msg = getString(fetchMedicationUiState.errorData.message),
                            positiveBtnMsg = getString(R.string.btn_ok)
                        )
                    }

                    if (fetchMedicationUiState.wrongProtectiveWord) {
                        binding.etProtectiveWord.error = "Invalid protective word. Try again."
                    }

                    if (fetchMedicationUiState.isRecordsUpdated) {
                        findNavController().popBackStack()
                    }
                }
            }
        }
    }

    private fun showLoader(value: Boolean) {
        binding.btnContinue.isEnabled = !value
        binding.progressBar.isVisible = value
    }

    private fun setUpToolbar() {
        binding.toolbar.tvTitle.show()
        binding.toolbar.tvTitle.text = "Restricted PharmaNet Records"
        binding.toolbar.ivLeftOption.apply {
            this.show()
            setImageResource(R.drawable.ic_scanner_close)
            setColorFilter(ContextCompat.getColor(context, R.color.primary_blue))
            setOnClickListener {
                findNavController().popBackStack()
            }
        }
    }
}
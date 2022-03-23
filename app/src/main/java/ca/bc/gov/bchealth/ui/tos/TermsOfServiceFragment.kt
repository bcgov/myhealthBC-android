package ca.bc.gov.bchealth.ui.tos

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import ca.bc.gov.bchealth.R
import ca.bc.gov.bchealth.databinding.FragmentTermsOfServiceBinding
import ca.bc.gov.bchealth.utils.toast
import ca.bc.gov.bchealth.utils.viewBindings
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

/**
 * @author Pinakin Kansara
 */
@AndroidEntryPoint
class TermsOfServiceFragment : Fragment(R.layout.fragment_terms_of_service) {

    private val termsOfServiceViewModel: TermsOfServiceViewModel by viewModels()
    private val binding by viewBindings(FragmentTermsOfServiceBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupToolBar()

        binding.btnCancel.setOnClickListener {
            requireContext().toast("In Next story")
        }

        binding.btnAgree.setOnClickListener {
            requireContext().toast("In Next story")
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.RESUMED) {

                termsOfServiceViewModel.tosUiState.collect { uiState ->
                    if (!uiState.tos.isNullOrBlank()) {
                        binding.wbTosContent.loadData(uiState.tos, "text/html; charset=UTF-8", null)
                    }

                    binding.progressBar.isVisible = uiState.showLoading
                }
            }
        }

        termsOfServiceViewModel.getTermsOfServices()
    }

    private fun setupToolBar() {
        binding.toolbar.apply {
            ivLeftOption.visibility = View.VISIBLE
            ivLeftOption.setImageResource(R.drawable.ic_action_back)
            ivLeftOption.setOnClickListener {
                findNavController().popBackStack()
            }

            tvTitle.visibility = View.VISIBLE
            tvTitle.text = getString(R.string.terms_of_service)

            line1.visibility = View.VISIBLE
        }
    }
}

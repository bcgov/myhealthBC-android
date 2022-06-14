package ca.bc.gov.bchealth.ui.tos

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import ca.bc.gov.bchealth.R
import ca.bc.gov.bchealth.databinding.FragmentTermsOfServiceBinding
import ca.bc.gov.bchealth.ui.BaseFragment
import ca.bc.gov.bchealth.utils.showNoInternetConnectionMessage
import ca.bc.gov.bchealth.utils.viewBindings
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

/**
 * @author Pinakin Kansara
 */
@AndroidEntryPoint
class TermsOfServiceFragment : BaseFragment(R.layout.fragment_terms_of_service) {

    private val termsOfServiceViewModel: TermsOfServiceViewModel by viewModels()
    private val binding by viewBindings(FragmentTermsOfServiceBinding::bind)

    companion object {
        const val TERMS_OF_SERVICE_STATUS = "TERMS_OF_SERVICE_STATUS"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        findNavController().previousBackStackEntry?.savedStateHandle
            ?.set(TERMS_OF_SERVICE_STATUS, TermsOfServiceStatus.DECLINED)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val webViewSettings = binding.wbTosContent.settings
        webViewSettings.textZoom = webViewSettings.textZoom + 10

        binding.btnCancel.setOnClickListener {
            findNavController().previousBackStackEntry?.savedStateHandle
                ?.set(TERMS_OF_SERVICE_STATUS, TermsOfServiceStatus.DECLINED)
            findNavController().popBackStack()
        }

        binding.btnAgree.setOnClickListener {
            findNavController().previousBackStackEntry?.savedStateHandle
                ?.set(TERMS_OF_SERVICE_STATUS, TermsOfServiceStatus.ACCEPTED)
            findNavController().popBackStack()
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.RESUMED) {

                termsOfServiceViewModel.tosUiState.collect { uiState ->
                    if (!uiState.tos.isNullOrBlank()) {
                        binding.wbTosContent.loadDataWithBaseURL(
                            "app:htmlPage",
                            uiState.tos, "text/html", "utf-8", null
                        )
                    }

                    binding.progressBar.isVisible = uiState.showLoading

                    if (!uiState.isConnected) {
                        binding.root.showNoInternetConnectionMessage(requireContext())
                    }
                }
            }
        }

        termsOfServiceViewModel.getTermsOfServices()
    }

    override fun setToolBar(appBarConfiguration: AppBarConfiguration) {
        with(binding.layoutToolbar.topAppBar) {
            setNavigationIcon(R.drawable.ic_toolbar_back)
            setNavigationOnClickListener {
                findNavController().popBackStack()
            }
            title = getString(R.string.terms_of_service)
        }
    }
}

enum class TermsOfServiceStatus {
    ACCEPTED,
    DECLINED
}

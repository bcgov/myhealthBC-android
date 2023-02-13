package ca.bc.gov.bchealth.ui.dependents.profile

import android.os.Bundle
import android.view.View
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import ca.bc.gov.bchealth.R
import ca.bc.gov.bchealth.compose.MyHealthTheme
import ca.bc.gov.bchealth.databinding.FragmentDependentProfileBinding
import ca.bc.gov.bchealth.ui.dependents.BaseDependentFragment
import ca.bc.gov.bchealth.utils.launchOnStart
import ca.bc.gov.bchealth.utils.showNoInternetConnectionMessage
import ca.bc.gov.bchealth.utils.toggleVisibility
import ca.bc.gov.bchealth.utils.viewBindings
import ca.bc.gov.common.exceptions.NetworkConnectionException
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DependentProfileFragment : BaseDependentFragment(R.layout.fragment_dependent_profile) {
    private val args: DependentProfileFragmentArgs by navArgs()
    private val viewModel: DependentProfileViewModel by viewModels()

    private val binding by viewBindings(FragmentDependentProfileBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupComposeToolbar(binding.composeToolbar.root, getString(R.string.dependents_profile))
        val patientId = args.patientId
        viewModel.loadInformation(patientId)

        launchOnStart { collectUiState() }
    }

    private suspend fun collectUiState() {
        viewModel.uiState.collect { uiState ->
            binding.viewLoading.root.toggleVisibility(uiState.isLoading)
            binding.btnRemove.isEnabled = uiState.dto != null

            uiState.dto?.let { dto ->
                binding.tvFullName.text = dto.getFullName()
                binding.btnRemove.setOnClickListener {
                    confirmDeletion(dto.patientId, dto.firstname)
                }
            }

            uiState.error?.let { handleError(it) }

            if (uiState.onDependentRemoved) {

                findNavController().navigate(R.id.action_dependentProfile_to_dependentList)
            } else if (uiState.dependentInfo.isNotEmpty()) {
                binding.composeBody.apply {
                    setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
                    setContent {
                        MyHealthTheme {
                            DependentProfileUI(uiState.dependentInfo)
                        }
                    }
                }
            }
        }
    }

    override fun deleteDependent(patientId: Long) {
        viewModel.removeDependent(patientId)
    }

    private fun handleError(e: Exception) {
        viewModel.resetErrorState()
        if (e is NetworkConnectionException) {
            context?.let {
                binding.root.showNoInternetConnectionMessage(it)
            }
        } else {
            showGenericError()
        }
    }
}

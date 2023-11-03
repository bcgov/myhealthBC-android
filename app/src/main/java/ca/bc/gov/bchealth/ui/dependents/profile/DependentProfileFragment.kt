package ca.bc.gov.bchealth.ui.dependents.profile

import android.os.Bundle
import android.view.View
import androidx.compose.runtime.Composable
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import ca.bc.gov.bchealth.R
import ca.bc.gov.bchealth.ui.dependents.BaseDependentFragment
import ca.bc.gov.bchealth.utils.showNoInternetConnectionMessage
import ca.bc.gov.common.exceptions.NetworkConnectionException
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DependentProfileFragment : BaseDependentFragment(null) {
    private val args: DependentProfileFragmentArgs by navArgs()
    private val viewModel: DependentProfileViewModel by viewModels()

    @Composable
    override fun GetComposableLayout() {
        DependentProfileUI(viewModel, { findNavController().popBackStack() }) { dto ->
            dto?.let {
                confirmDeletion(dto.patientId, dto.firstname)
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val patientId = args.patientId
        viewModel.loadInformation(patientId)

        collectUiState()
    }

    private fun collectUiState() {
        viewModel.uiState.collectOnStart { uiState ->
            uiState.error?.let { handleError(it) }

            if (uiState.onDependentRemoved) {
                findNavController().navigate(R.id.action_dependentProfile_to_dependentList)
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
                view?.rootView?.showNoInternetConnectionMessage(it)
            }
        } else {
            showGenericError()
        }
    }
}

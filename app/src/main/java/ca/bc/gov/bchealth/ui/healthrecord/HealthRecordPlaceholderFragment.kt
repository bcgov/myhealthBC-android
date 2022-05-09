package ca.bc.gov.bchealth.ui.healthrecord

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import ca.bc.gov.bchealth.R
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

/*
* HealthRecordPlaceholderFragment will act like placeholder fragment.
* It will launch IndividualHealthRecordFragment or healthRecordsFragment or addHealthRecordsFragment
* based on number of patients.
* observeNavigationFlow() will decide whether to recheck the above flow or just navigate back.
* observeNavigationFlow() is required as user will never stay on this fragment.
* */
@AndroidEntryPoint
class HealthRecordPlaceholderFragment : Fragment(R.layout.health_record_placeholder_fragment) {

    private val viewModel: HealthRecordPlaceholderViewModel by viewModels()
    private var isHealthRecordsFlowActive = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (!isHealthRecordsFlowActive) {
            isHealthRecordsFlowActive = true
            collectHealthRecordsFlow()
        }
        observeNavigationFlow()
    }

    private fun observeNavigationFlow() {
        findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<NavigationAction>(
            PLACE_HOLDER_NAVIGATION
        )?.observe(viewLifecycleOwner) {
            findNavController().currentBackStackEntry?.savedStateHandle?.remove<NavigationAction>(
                PLACE_HOLDER_NAVIGATION
            )
            it?.let {
                when (it) {
                    NavigationAction.ACTION_BACK -> {
                        findNavController().popBackStack()
                    }
                    NavigationAction.ACTION_RE_CHECK -> {
                        collectHealthRecordsFlow()
                    }
                }
            }
        }
    }

    private fun collectHealthRecordsFlow() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.uiState.collect { uiState ->
                    navigate(uiState)
                }
            }
        }
        viewModel.getBcscAuthPatient()
    }

    private fun navigate(uiState: PatientRecordsState) {
        if (uiState.isBcscAuthenticatedPatientAvailable != null) {
            if (uiState.isBcscAuthenticatedPatientAvailable == BcscAuthPatientAvailability.AVAILABLE) {
                if (uiState.bcscAuthenticatedPatientDto != null) {
                    val action =
                        HealthRecordPlaceholderFragmentDirections
                            .actionHealthRecordsPlaceHolderFragmentToIndividualHealthRecordFragment(
                                uiState.bcscAuthenticatedPatientDto.id,
                                uiState.bcscAuthenticatedPatientDto.fullName,
                                uiState.bcscAuthenticatedPatientDto.authenticationStatus.name
                            )
                    findNavController().navigate(action)
                }
            } else {
                findNavController().navigate(R.id.addHealthRecordsFragment)
            }
        }
    }

    companion object {
        const val PLACE_HOLDER_NAVIGATION = "PLACE_HOLDER_NAVIGATION"
    }
}

enum class NavigationAction {
    ACTION_BACK,
    ACTION_RE_CHECK
}

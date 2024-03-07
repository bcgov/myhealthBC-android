package ca.bc.gov.bchealth.ui.healthrecord

import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.fragment.findNavController
import ca.bc.gov.bchealth.BcVaccineCardNavGraphDirections
import ca.bc.gov.bchealth.R
import ca.bc.gov.bchealth.compose.component.HGTopAppBar
import ca.bc.gov.bchealth.compose.component.menu.TopAppBarActionItem
import ca.bc.gov.bchealth.compose.theme.HealthGatewayTheme
import ca.bc.gov.bchealth.model.BcServiceCardLoginInfoType
import ca.bc.gov.bchealth.model.BcServiceCardSessionInfoType
import ca.bc.gov.bchealth.ui.BaseSecureFragment
import ca.bc.gov.bchealth.ui.BaseViewModel
import ca.bc.gov.bchealth.ui.NavigationAction
import ca.bc.gov.bchealth.ui.healthrecord.filter.PatientFilterViewModel
import ca.bc.gov.bchealth.ui.login.BcscAuthViewModel
import ca.bc.gov.bchealth.ui.login.LoginStatus
import ca.bc.gov.bchealth.utils.redirect
import ca.bc.gov.bchealth.utils.showErrorSnackbar
import ca.bc.gov.bchealth.viewmodel.SharedViewModel
import dagger.hilt.android.AndroidEntryPoint

/**
 * @author Pinakin Kansara
 */
@AndroidEntryPoint
class HealthRecordFragment : BaseSecureFragment(null) {
    private val bcscAuthViewModel: BcscAuthViewModel by viewModels()
    private val healthRecordViewModel: HealthRecordViewModel by viewModels()
    private val filterViewModel: PatientFilterViewModel by activityViewModels()
    private val sharedViewModel: SharedViewModel by activityViewModels()

    override fun getBaseViewModel(): BaseViewModel {
        return healthRecordViewModel
    }

    @Composable
    override fun GetComposableLayout() {

        val authState =
            bcscAuthViewModel.authStatus.collectAsStateWithLifecycle(minActiveState = Lifecycle.State.RESUMED).value
        val menuItems = mutableListOf<TopAppBarActionItem>(
            TopAppBarActionItem.IconActionItem.ShowIfRoom(
                title = getString(R.string.settings),
                onClick = { healthRecordViewModel.executeOneTimeDataFetch() },
                icon = R.drawable.ic_refresh,
                contentDescription = getString(R.string.refresh),
            ),
            TopAppBarActionItem.IconActionItem.ShowIfRoom(
                title = getString(R.string.settings),
                onClick = { findNavController().navigate(R.id.settingsFragment) },
                icon = R.drawable.ic_menu_settings,
                contentDescription = getString(R.string.settings),
            )
        )
        HealthGatewayTheme {
            Scaffold(
                topBar = {
                    HGTopAppBar(
                        title = authState.userName ?: stringResource(id = R.string.home),
                        actionItems = menuItems
                    )
                },
                content = {
                    HealthRecordScreen(
                        onRequireAuthentication = ::onRequireAuthentication,
                        onHealthRecordItemClicked = ::onHealthRecordItemClicked,
                        onFilterClicked = ::onFilterClicked,
                        onUnlockMedicationRecords = ::onUnlockMedicationRecords,
                        onLinkClick = ::onLinkClick,
                        onNetworkError = ::onNetworkError,
                        onServiceDownError = ::onServiceDownError,
                        onDateError = ::onDateError,
                        Modifier
                            .statusBarsPadding()
                            .navigationBarsPadding()
                            .padding(it),
                        bcscAuthViewModel,
                        healthRecordViewModel,
                        filterViewModel,
                        sharedViewModel
                    )
                }
            )
        }
    }

    override fun handleNavigationAction(navigationAction: NavigationAction) {
        when (navigationAction) {
            NavigationAction.ACTION_BACK -> {
                findNavController().popBackStack()
            }

            NavigationAction.ACTION_RE_CHECK -> {
                bcscAuthViewModel.checkSession()
            }
        }
    }

    private fun onRequireAuthentication(loginStatus: LoginStatus) {
        when (loginStatus) {
            LoginStatus.ACTIVE -> {
                // No operation
            }

            LoginStatus.EXPIRED -> {
                val action =
                    BcVaccineCardNavGraphDirections.actionGlobalBcServiceCardSessionFragment(
                        BcServiceCardSessionInfoType.RECORDS
                    )
                findNavController().navigate(action)
            }

            LoginStatus.NOT_AUTHENTICATED -> {
                val action =
                    BcVaccineCardNavGraphDirections.actionGlobalBcServicesCardLoginFragment(
                        BcServiceCardLoginInfoType.RECORDS
                    )
                findNavController().navigate(action)
            }
        }
    }

    private fun onHealthRecordItemClicked(healthRecordItem: HealthRecordItem) {
        when (healthRecordItem.healthRecordType) {
            HealthRecordType.COVID_TEST_RECORD -> {
                val action = HealthRecordFragmentDirections
                    .actionHealthRecordFragmentToCovidTestResultDetailFragment(healthRecordItem.recordId)
                findNavController().navigate(action)
            }

            HealthRecordType.HEALTH_VISIT_RECORD -> {
                val action = HealthRecordFragmentDirections
                    .actionHealthRecordFragmentToHealthVisitDetailsFragment(healthRecordItem.recordId)
                findNavController().navigate(action)
            }

            HealthRecordType.MEDICATION_RECORD -> {
                val action = HealthRecordFragmentDirections
                    .actionHealthRecordFragmentToMedicationDetailFragment(healthRecordItem.recordId)
                findNavController().navigate(action)
            }

            HealthRecordType.IMMUNIZATION_RECORD -> {
                val action = HealthRecordFragmentDirections
                    .actionHealthRecordFragmentToImmunizationRecordDetailFragment(healthRecordItem.recordId)
                findNavController().navigate(action)
            }

            HealthRecordType.CLINICAL_DOCUMENT_RECORD -> {
                val action = HealthRecordFragmentDirections
                    .actionHealthRecordFragmentToClinicalDocumentDetailsFragment(healthRecordItem.recordId)
                findNavController().navigate(action)
            }

            HealthRecordType.HOSPITAL_VISITS_RECORD -> {
                val action = HealthRecordFragmentDirections
                    .actionHealthRecordFragmentToHospitalVisitDetailsFragment(healthRecordItem.recordId)
                findNavController().navigate(action)
            }

            HealthRecordType.LAB_RESULT_RECORD -> {
                val action = HealthRecordFragmentDirections
                    .actionHealthRecordFragmentToLabTestDetailFragment(healthRecordItem.recordId)
                findNavController().navigate(action)
            }

            HealthRecordType.SPECIAL_AUTHORITY_RECORD -> {
                val action = HealthRecordFragmentDirections
                    .actionHealthRecordFragmentToSpecialAuthorityDetailsFragment(healthRecordItem.recordId)
                findNavController().navigate(action)
            }

            HealthRecordType.DIAGNOSTIC_IMAGING -> {
                val action = HealthRecordFragmentDirections
                    .actionHealthRecordFragmentToDiagnosticImagingDetailFragment(healthRecordItem.recordId)
                findNavController().navigate(action)
            }

            HealthRecordType.BC_CANCER_SCREENING -> {
                val action = HealthRecordFragmentDirections
                    .actionHealthRecordFragmentToBcCancerScreeningDetailFragment(healthRecordItem.recordId)
                findNavController().navigate(action)
            }
        }
    }

    private fun onFilterClicked() {
        findNavController().navigate(R.id.filterFragment)
    }

    private fun onUnlockMedicationRecords(patientId: Long) {
        val action =
            HealthRecordFragmentDirections.actionHealthRecordFragmentToProtectiveWordFragment(
                patientId
            )
        findNavController().navigate(action)
    }

    private fun onNetworkError() {
        showNoInternetConnectionMessage()
    }

    private fun onServiceDownError() {
        showServiceDownMessage()
    }

    private fun onDateError() {
        view?.let {
            it.showErrorSnackbar(requireContext().getString(R.string.service_down))
        }
    }

    private fun onLinkClick(link: String) {
        requireActivity().redirect(link)
    }
}

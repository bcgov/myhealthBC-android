package ca.bc.gov.bchealth.ui.healthrecord

import android.os.Bundle
import android.view.View
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import androidx.work.WorkInfo
import ca.bc.gov.bchealth.R
import ca.bc.gov.bchealth.compose.MyHealthTheme
import ca.bc.gov.bchealth.ui.BaseSecureFragment
import ca.bc.gov.bchealth.ui.BaseViewModel
import ca.bc.gov.bchealth.ui.NavigationAction
import ca.bc.gov.bchealth.ui.custom.MyHealthToolBar
import ca.bc.gov.bchealth.ui.login.BcscAuthViewModel
import ca.bc.gov.bchealth.ui.login.LoginStatus
import ca.bc.gov.bchealth.utils.launchAndRepeatWithLifecycle
import ca.bc.gov.bchealth.utils.observeWork
import ca.bc.gov.repository.bcsc.BACKGROUND_AUTH_RECORD_FETCH_WORK_NAME
import dagger.hilt.android.AndroidEntryPoint

/**
 * @author Pinakin Kansara
 */
@AndroidEntryPoint
class HealthRecordFragment : BaseSecureFragment(null) {
    private val bcscAuthViewModel: BcscAuthViewModel by viewModels()
    private val healthRecordViewModel: HealthRecordViewModel by viewModels()

    override fun getBaseViewModel(): BaseViewModel {
        return healthRecordViewModel
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        launchAndRepeatWithLifecycle(Lifecycle.State.RESUMED) {
            bcscAuthViewModel.authStatus.collect { state ->
                if (state.showLoading) {
                    healthRecordViewModel.showProgressBar()
                } else {
                    when (state.loginStatus) {
                        LoginStatus.NOT_AUTHENTICATED -> {
                            findNavController().navigate(R.id.bcServicesCardLoginFragment)
                        }

                        LoginStatus.EXPIRED -> {
                            findNavController().navigate(R.id.bcServiceCardSessionFragment)
                        }

                        LoginStatus.ACTIVE -> {
                            observeHealthRecordsSyncCompletion()
                        }
                    }
                }
            }
        }
        bcscAuthViewModel.checkSession()
    }

    @Composable
    override fun GetComposableLayout() {
        val state = bcscAuthViewModel.authStatus.collectAsState().value
        MyHealthTheme {
            Scaffold(
                topBar = {
                    MyHealthToolBar(
                        title = state.userName ?: "",
                        isCenterAligned = false,
                        actions = {
                            IconButton(onClick = { findNavController().navigate(R.id.settingsFragment) }) {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_settings),
                                    contentDescription = stringResource(
                                        id = R.string.settings
                                    ),
                                    tint = MaterialTheme.colors.primary
                                )
                            }
                        }
                    )
                },
                content = { it ->
                    HealthRecordScreen(
                        modifier = Modifier
                            .statusBarsPadding()
                            .navigationBarsPadding()
                            .padding(it),
                        viewModel = healthRecordViewModel,
                        onUnlockMedicationRecords = {
                            state.patient?.id?.let {
                                val action =
                                    HealthRecordFragmentDirections.actionHealthRecordFragmentToProtectiveWordFragment(
                                        it
                                    )
                                findNavController().navigate(action)
                            }
                        },
                        onClick = { record ->
                            onClick(record)
                        },
                        onSwipeToRefresh = { healthRecordViewModel.executeOneTimeDataFetch() },
                        onAddNotesClicked = { findNavController().navigate(R.id.addNotesFragment) }
                    )
                },
                contentColor = contentColorFor(backgroundColor = MaterialTheme.colors.background)
            )
        }
    }

    private fun onClick(healthRecordItem: HealthRecordItem) {

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
        }
    }

    private fun observeHealthRecordsSyncCompletion() {
        observeWork(BACKGROUND_AUTH_RECORD_FETCH_WORK_NAME) { state ->
            if (state == WorkInfo.State.RUNNING) {
                healthRecordViewModel.showProgressBar()
            } else {
                healthRecordViewModel.showTimeLine()
            }
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
}

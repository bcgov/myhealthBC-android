package ca.bc.gov.bchealth.ui.healthrecord

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.work.WorkManager
import ca.bc.gov.bchealth.R
import ca.bc.gov.bchealth.compose.BasePreview
import ca.bc.gov.bchealth.compose.component.BCCancerBannerUi
import ca.bc.gov.bchealth.compose.component.EmptyStateUI
import ca.bc.gov.bchealth.compose.component.HGCircularProgressIndicator
import ca.bc.gov.bchealth.compose.component.HealthRecordItemUI
import ca.bc.gov.bchealth.compose.component.HorizontalFilterGroupUI
import ca.bc.gov.bchealth.compose.component.ImmunizationBannerUI
import ca.bc.gov.bchealth.compose.component.SearchBarUI
import ca.bc.gov.bchealth.compose.theme.HealthGatewayTheme
import ca.bc.gov.bchealth.ui.healthrecord.filter.PatientFilterViewModel
import ca.bc.gov.bchealth.ui.login.AuthStatus
import ca.bc.gov.bchealth.ui.login.BcscAuthViewModel
import ca.bc.gov.bchealth.ui.login.LoginStatus
import ca.bc.gov.bchealth.utils.URL_BC_CANCER_BANNER
import ca.bc.gov.bchealth.viewmodel.SharedViewModel
import ca.bc.gov.common.BuildConfig
import ca.bc.gov.repository.bcsc.BACKGROUND_AUTH_RECORD_FETCH_WORK_NAME

private const val TAG = "HealthRecordScreen"

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun HealthRecordScreen(
    onRequireAuthentication: (LoginStatus) -> Unit,
    onHealthRecordItemClicked: (HealthRecordItem) -> Unit,
    onFilterClicked: () -> Unit,
    onUnlockMedicationRecords: (Long) -> Unit,
    onLinkClick: (String) -> Unit,
    onNetworkError: () -> Unit,
    onServiceDownError: () -> Unit,
    onDateError: () -> Unit,
    modifier: Modifier = Modifier,
    authViewModel: BcscAuthViewModel,
    healthRecordViewModel: HealthRecordViewModel,
    filterViewModel: PatientFilterViewModel,
    sharedViewModel: SharedViewModel,
) {

    val uiState by healthRecordViewModel.uiState.collectAsStateWithLifecycle()
    val authState: AuthStatus by authViewModel.authStatus.collectAsStateWithLifecycle()

    LaunchedEffect(key1 = Unit) {
        authViewModel.checkSession()
    }

    authState.loginStatus?.let { loginStatus ->
        when (loginStatus) {
            LoginStatus.ACTIVE -> {
            }

            LoginStatus.EXPIRED,
            LoginStatus.NOT_AUTHENTICATED -> {
                LaunchedEffect(key1 = loginStatus) {
                    authViewModel.resetAuthStatus()
                    onRequireAuthentication(loginStatus)
                }
            }
        }
    }

    val context = LocalContext.current
    val workRequest = WorkManager.getInstance(context)
        .getWorkInfosForUniqueWorkLiveData(BACKGROUND_AUTH_RECORD_FETCH_WORK_NAME)
        .observeAsState()
    val workState = workRequest.value?.firstOrNull()?.state

    if (workState != null && workState.isFinished) {
        LaunchedEffect(key1 = Unit) {
            healthRecordViewModel.showTimeLine(filterViewModel.getFilterString())
        }
    } else {
        LaunchedEffect(key1 = Unit) {
            healthRecordViewModel.showProgressBar()
        }
    }

    if (!uiState.isConnected) {
        LaunchedEffect(key1 = Unit) {
            onNetworkError()
            healthRecordViewModel.resetErrorState()
        }
    }

    if (!uiState.isHgServicesUp) {
        LaunchedEffect(key1 = Unit) {
            onServiceDownError()
            healthRecordViewModel.resetErrorState()
        }
    }

    if (uiState.dateError) {
        LaunchedEffect(key1 = Unit) {
            onDateError()
            healthRecordViewModel.resetErrorState()
        }
    }

    HealthRecordScreenContent(
        onPullToRefresh = healthRecordViewModel::executeOneTimeDataFetch,
        onHealthRecordItemClicked = onHealthRecordItemClicked,
        onFilterClicked = onFilterClicked,
        onFilterCleared = {
            filterViewModel.clearFilter()
            healthRecordViewModel.showTimeLine(filterViewModel.getFilterString())
        },
        onSearchQuery = {
            filterViewModel.updateFilter(it)
            healthRecordViewModel.showTimeLine(filterViewModel.getFilterString())
        },
        onUnlockMedicationRecords = {
            authState.patient?.id?.let {
                onUnlockMedicationRecords(it)
            }
        },
        onLinkClick = onLinkClick,
        onDismissClick = { sharedViewModel.displayImmunizationBanner = false },
        modifier = modifier,
        uiState = uiState,
        displayImmunizationBanner = sharedViewModel.displayImmunizationBanner
    )
}

@ExperimentalMaterialApi
@Composable
private fun HealthRecordScreenContent(
    onPullToRefresh: () -> Unit,
    onHealthRecordItemClicked: (HealthRecordItem) -> Unit,
    onFilterClicked: () -> Unit,
    onFilterCleared: () -> Unit,
    onSearchQuery: (String) -> Unit,
    onUnlockMedicationRecords: () -> Unit,
    onLinkClick: (String) -> Unit,
    onDismissClick: () -> Unit,
    modifier: Modifier = Modifier,
    uiState: HealthRecordUiState,
    displayImmunizationBanner: Boolean
) {
    val pullToRefresh = rememberPullRefreshState(
        refreshing = uiState.isLoading,
        onRefresh = onPullToRefresh
    )
    Box(modifier = Modifier.pullRefresh(pullToRefresh)) {

        if (uiState.isLoading) {
            HGCircularProgressIndicator()
        } else {
            Column(
                modifier = modifier
                    .padding(16.dp)
                    .fillMaxSize()
            ) {
                SearchBarUI(modifier = Modifier.fillMaxWidth(), onFilterClicked = onFilterClicked, onSearchQuery = onSearchQuery)
                Spacer(modifier = Modifier.height(16.dp))
                if (uiState.filters.isNotEmpty()) {
                    HorizontalFilterGroupUI(
                        onFilterCleared = onFilterCleared,
                        modifier = Modifier.fillMaxWidth(),
                        filtersApplied = uiState.filters
                    )
                }
                HealthRecordList(
                    onHealthRecordItemClicked = onHealthRecordItemClicked,
                    onUnlockMedicationRecords = onUnlockMedicationRecords,
                    onLinkClick = onLinkClick,
                    onDismissClick = onDismissClick,
                    uiState = uiState,
                    displayImmunizationBanner = displayImmunizationBanner
                )
            }
        }
        PullRefreshIndicator(
            uiState.isLoading,
            pullToRefresh,
            Modifier.align(Alignment.TopCenter)
        )
    }
}

@Composable
private fun HealthRecordList(
    onHealthRecordItemClicked: (HealthRecordItem) -> Unit,
    onUnlockMedicationRecords: () -> Unit,
    onLinkClick: (String) -> Unit,
    onDismissClick: () -> Unit,
    modifier: Modifier = Modifier,
    uiState: HealthRecordUiState,
    displayImmunizationBanner: Boolean
) {

    LazyColumn(
        modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        if (BuildConfig.FLAG_IMMZ_BANNER && displayImmunizationBanner) {
            item {
                ImmunizationBannerUI(
                    onDismissClick = onDismissClick,
                    onLinkClick = onLinkClick,
                    body1 = stringResource(id = R.string.records_immunization_banner_top),
                    clickableText = stringResource(id = R.string.records_immunization_banner_click),
                    body2 = stringResource(id = R.string.records_immunization_banner_bottom)
                )
            }
        }

        if (uiState.requiredProtectiveWordVerification) {
            item {
                HiddenMedicationRecordUI(onUnlockMedicationRecords = onUnlockMedicationRecords)
            }
        }

        if (uiState.showBCCancerBanner) {
            item {
                BCCancerBannerUi(
                    onLinkClick = { onLinkClick(URL_BC_CANCER_BANNER) },
                    body1 = stringResource(id = R.string.bc_cancer_banner),
                    clickableText = stringResource(id = R.string.bc_cancer_learn_more),
                    body2 = stringResource(id = R.string.bc_cancer_learn_more)
                )
            }
        }

        if (uiState.healthRecords.isEmpty()) {
            item {
                EmptyStateUI(
                    image = R.drawable.ic_no_record,
                    title = R.string.no_records_found,
                    description = R.string.refresh
                )
            }
        }

        items(uiState.healthRecords) { record ->
            HealthRecordItemUI(
                onClick = { onHealthRecordItemClicked(record) },
                image = record.icon,
                title = record.title,
                description = record.description
            )
        }
    }
}

@Composable
@BasePreview
private fun HealthRecordScreenPreview() {
    HealthGatewayTheme {
    }
}

package ca.bc.gov.bchealth.ui.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.ConstraintSet
import androidx.work.WorkInfo
import androidx.work.WorkManager
import ca.bc.gov.bchealth.R
import ca.bc.gov.bchealth.compose.BasePreview
import ca.bc.gov.bchealth.compose.component.AnnouncementBannerUI
import ca.bc.gov.bchealth.compose.component.HGProgressIndicator
import ca.bc.gov.bchealth.compose.component.HGTextButton
import ca.bc.gov.bchealth.compose.component.LoginInfoCardUI
import ca.bc.gov.bchealth.compose.component.QuickAccessTileItemUI
import ca.bc.gov.bchealth.compose.theme.HealthGatewayTheme
import ca.bc.gov.bchealth.compose.theme.blue
import ca.bc.gov.bchealth.compose.theme.statusBlue
import ca.bc.gov.bchealth.compose.theme.white
import ca.bc.gov.bchealth.ui.login.BcscAuthViewModel
import ca.bc.gov.bchealth.ui.login.LoginStatus
import ca.bc.gov.bchealth.viewmodel.SharedViewModel
import ca.bc.gov.repository.bcsc.BACKGROUND_AUTH_RECORD_FETCH_WORK_NAME

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    authViewModel: BcscAuthViewModel,
    viewModel: HomeViewModel,
    sharedViewModel: SharedViewModel,
    onLoginClick: () -> Unit,
    onManageClick: () -> Unit,
    onOnBoardingRequired: (isReOnBoarding: Boolean) -> Unit,
    onBiometricAuthenticationRequired: () -> Unit,
    onQuickAccessTileClicked: (QuickAccessTileItem) -> Unit,
) {
    val uiState = viewModel.uiState.collectAsState().value

    val authState = authViewModel.authStatus.collectAsState().value

    LaunchedEffect(key1 = Unit) {
        viewModel.launchCheck()
    }

    if (uiState.isLoading) {
        HGProgressIndicator(modifier)
    } else {
        uiState.launchCheckStatus?.let {
            when (it) {
                LaunchCheckStatus.REQUIRE_ON_BOARDING -> {
                    LaunchedEffect(key1 = Unit) {
                        onOnBoardingRequired(false)
                        viewModel.resetUIState()
                    }
                }

                LaunchCheckStatus.REQUIRE_RE_ON_BOARDING -> {
                    LaunchedEffect(key1 = Unit) {
                        onOnBoardingRequired(true)
                        viewModel.resetUIState()
                    }
                }

                LaunchCheckStatus.REQUIRE_BIOMETRIC_AUTHENTICATION -> {
                    LaunchedEffect(key1 = Unit) {
                        onBiometricAuthenticationRequired()
                    }
                }

                LaunchCheckStatus.SUCCESS -> {
                    LaunchedEffect(key1 = Unit) {
                        authViewModel.checkSession()
                    }
                }
            }
        }
    }

    if (authState.showLoading) {
        HGProgressIndicator(modifier)
    } else {
        authState.loginStatus?.let {

            val context = LocalContext.current
            val workRequest = WorkManager.getInstance(context)
                .getWorkInfosForUniqueWorkLiveData(BACKGROUND_AUTH_RECORD_FETCH_WORK_NAME)
                .observeAsState()
            if (workRequest.value?.firstOrNull()?.state == WorkInfo.State.RUNNING) {
            } else {
                LaunchedEffect(key1 = Unit) {
                    viewModel.loadQuickAccessTiles(it)
                }
            }

            if (sharedViewModel.shouldFetchBanner) {
                LaunchedEffect(key1 = Unit) {
                    viewModel.fetchBanner()
                }
            }

            HomeScreenContent(
                modifier,
                onLoginClick,
                onQuickAccessTileClicked,
                onManageClick,
                onDismissClick = {
                    sharedViewModel.shouldFetchBanner = false
                    viewModel.dismissBanner()
                },
                onDismissTutorialClicked = { viewModel.tutorialDismissed() },
                it,
                viewModel.getLoginInfoCardData(it),
                uiState.bannerItem,
                uiState.quickAccessTileItems,
                uiState.isQuickAccessTileTutorialRequired
            )
        }
    }
}

@Composable
private fun HomeScreenContent(
    modifier: Modifier = Modifier,
    onLoginClick: () -> Unit,
    onQuickAccessTileClicked: (QuickAccessTileItem) -> Unit,
    onManageClick: () -> Unit,
    onDismissClick: () -> Unit,
    onDismissTutorialClicked: () -> Unit,
    loginStatus: LoginStatus,
    loginInfoCardData: LoginInfoCardData?,
    bannerItem: HomeBannerItem?,
    quickAccessTileItems: List<QuickAccessTileItem>,
    isQuickAccessTileTutorialRequired: Boolean
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(32.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalArrangement = Arrangement.spacedBy(20.dp)
    ) {

        bannerItem?.let { banner ->
            if (!banner.isDismissed) {
                item(span = { GridItemSpan(maxLineSpan) }) {
                    AnnouncementBannerUI(
                        title = banner.title,
                        description = banner.body,
                        showReadMore = banner.showReadMore(),
                        onLearnMoreClick = { /*TODO*/ },
                        onDismissClick = { onDismissClick() }
                    )
                }
            }
        }

        loginInfoCardData?.let { data ->
            item(span = { GridItemSpan(maxLineSpan) }) {
                LoginInfoCardUI(
                    onClick = { onLoginClick() },
                    title = stringResource(id = data.title),
                    description = stringResource(id = data.description),
                    buttonText = stringResource(id = data.buttonText),
                    image = if (data.image > 0) {
                        painterResource(id = data.image)
                    } else {
                        null
                    }
                )
            }
        }

        item(span = { GridItemSpan(maxLineSpan) }) {
            QuickAccessHeaderUI(onManageClick, onDismissTutorialClicked, loginStatus, isQuickAccessTileTutorialRequired)
        }

        items(quickAccessTileItems) {
            QuickAccessTileItemUI(
                onClick = { onQuickAccessTileClicked(it) },
                icon = painterResource(id = it.icon),
                title = it.name
            )
        }
    }
}

@Composable
private fun QuickAccessHeaderUI(onManageClick: () -> Unit, onDismissTutorialClicked: () -> Unit, loginStatus: LoginStatus, isQuickAccessTileTutorialRequired: Boolean) {

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = stringResource(id = R.string.quick_access),
            style = MaterialTheme.typography.subtitle2,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colors.primary
        )

        AnimatedVisibility(visible = (LoginStatus.ACTIVE == loginStatus)) {
            HGTextButton(onClick = onManageClick) {
                Text(
                    text = stringResource(id = R.string.manage),
                    style = MaterialTheme.typography.body2,
                    fontWeight = FontWeight.Bold,
                    color = blue,
                    textDecoration = TextDecoration.Underline
                )
            }
            if (isQuickAccessTileTutorialRequired) {
                QuickAccessManagementTutorialUI(onDismissTutorialClicked)
            }
        }
    }
}

private const val anchorIconId = "anchorIconId"
private const val bannerBodyId = "bannerBodyId"

private fun quickAccessManagementTutorialConstraint(): ConstraintSet {
    return ConstraintSet {
        val anchorIcon = createRefFor(anchorIconId)
        val body = createRefFor(bannerBodyId)

        constrain(anchorIcon) {
            top.linkTo(parent.top, 16.dp)
            end.linkTo(parent.end, 16.dp)
        }

        constrain(body) {
            start.linkTo(parent.start)
            top.linkTo(anchorIcon.bottom)
            end.linkTo(parent.end)
            bottom.linkTo(parent.bottom)
        }
    }
}

@Composable
private fun QuickAccessManagementTutorialUI(onDismissTutorialClicked: () -> Unit) {
    Popup(
        alignment = Alignment.TopEnd,
        offset = IntOffset(0, 100)
    ) {
        BoxWithConstraints(modifier = Modifier.wrapContentSize()) {

            ConstraintLayout(constraintSet = quickAccessManagementTutorialConstraint()) {
                Image(
                    modifier = Modifier.layoutId(anchorIconId),
                    painter = painterResource(id = R.drawable.ic_anchor),
                    contentDescription = null
                )
                Column(
                    modifier = Modifier
                        .layoutId(bannerBodyId)
                        .background(statusBlue)
                        .padding(start = 8.dp, top = 8.dp, end = 8.dp)
                ) {
                    Text(
                        modifier = Modifier.padding(start = 8.dp, end = 8.dp),
                        text = stringResource(id = R.string.manage_hint),
                        style = MaterialTheme.typography.body2,
                        color = white
                    )
                    HGTextButton(onClick = { onDismissTutorialClicked() }) {
                        Text(
                            text = "Got it",
                            style = MaterialTheme.typography.body2,
                            fontWeight = FontWeight.Bold,
                            color = white,
                            textDecoration = TextDecoration.Underline
                        )
                    }
                }
            }
        }
    }
}

@Composable
@BasePreview
private fun HomeScreenNonAuthenticatedPreview() {
    HealthGatewayTheme {
        HomeScreenContent(
            onLoginClick = {},
            onManageClick = {},
            onQuickAccessTileClicked = {},
            onDismissClick = {},
            onDismissTutorialClicked = {},
            loginStatus = LoginStatus.NOT_AUTHENTICATED,
            loginInfoCardData = null,
            bannerItem = null,
            quickAccessTileItems = emptyList(),
            isQuickAccessTileTutorialRequired = false
        )
    }
}

@Composable
@BasePreview
private fun HomeScreenAuthenticatedPreview() {
    HealthGatewayTheme {
        HomeScreenContent(
            onLoginClick = {},
            onManageClick = {},
            onQuickAccessTileClicked = {},
            onDismissClick = {},
            onDismissTutorialClicked = {},
            loginStatus = LoginStatus.ACTIVE,
            loginInfoCardData = null,
            bannerItem = null,
            quickAccessTileItems = emptyList(),
            isQuickAccessTileTutorialRequired = false
        )
    }
}

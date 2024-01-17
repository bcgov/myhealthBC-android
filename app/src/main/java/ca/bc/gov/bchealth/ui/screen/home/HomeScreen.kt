package ca.bc.gov.bchealth.ui.screen.home

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import ca.bc.gov.bchealth.R
import ca.bc.gov.bchealth.Screen
import ca.bc.gov.bchealth.compose.MultiDevicePreview
import ca.bc.gov.bchealth.compose.component.m3.AnnouncementBannerUI
import ca.bc.gov.bchealth.compose.component.m3.LoginInfoCardUI
import ca.bc.gov.bchealth.compose.component.m3.QuickAccessTileItemUI
import ca.bc.gov.bchealth.compose.theme.m3.HealthGatewayTheme
import ca.bc.gov.bchealth.ui.auth.BioMetricState
import ca.bc.gov.bchealth.ui.auth.BiometricsAuthenticationFragment
import ca.bc.gov.bchealth.ui.home.LaunchCheckStatus
import ca.bc.gov.bchealth.ui.home.QuickAccessTileItem

/**
 * @author pinakin.kansara
 * Created 2023-12-12 at 3:10 p.m.
 */
private const val TAG = "HomeScreen"

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = hiltViewModel(),
    navController: NavController
) {

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val backStack = navController.currentBackStackEntry
    val flow = backStack?.savedStateHandle?.getStateFlow(
        BiometricsAuthenticationFragment.BIOMETRIC_STATE,
        BioMetricState.NULL
    )?.collectAsStateWithLifecycle()

    LaunchedEffect(key1 = flow?.value) {
        Log.d(TAG, "Back Entry = ${flow?.value} ")
        when (flow?.value) {
            BioMetricState.FAILED -> {
                navController.popBackStack()
            }

            BioMetricState.SUCCESS -> {
                viewModel.fetchBanner()
            }

            else -> {}
        }
    }

    when (uiState.launchCheckStatus) {
        LaunchCheckStatus.REQUIRE_ON_BOARDING -> {
            LaunchedEffect(key1 = uiState.launchCheckStatus) {
                viewModel.resetUiState()
                Log.d(TAG, "launchCheckStatus = ${uiState.launchCheckStatus}")
                navController.navigate(Screen.OnBoarding.route)
            }
        }

        LaunchCheckStatus.REQUIRE_RE_ON_BOARDING -> {}
        LaunchCheckStatus.REQUIRE_BIOMETRIC_AUTHENTICATION -> {
            LaunchedEffect(key1 = uiState.launchCheckStatus) {
                navController.navigate(Screen.BiometricAuthentication.route)
                viewModel.resetUiState()
            }
        }

        LaunchCheckStatus.SUCCESS -> {}
        null -> {
            LaunchedEffect(key1 = Unit) {
                viewModel.launchCheck()
                Log.d(TAG, "launchCheck()")
            }
        }
    }

    HomeScreenContent(
        modifier,
        onDismissClick = {
            viewModel.dismissBanner()
        },
        onLoginClick = {},
        onDismissTutorialClicked = {
            viewModel.tutorialDismissed()
        },
        uiState
    )
}

@Composable
private fun HomeScreenContent(
    modifier: Modifier = Modifier,
    onDismissClick: () -> Unit,
    onLoginClick: () -> Unit,
    onDismissTutorialClicked: () -> Unit,
    uiState: HomeUiState
) {
    BoxWithConstraints {
        val cellCount = when {
            maxWidth < 600.dp -> 2
            maxWidth < 840.dp -> 3
            else -> 4
        }
        LazyVerticalGrid(
            columns = GridCells.Fixed(cellCount),
            modifier = modifier.fillMaxSize(),
            contentPadding = PaddingValues(32.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            uiState.bannerItem?.let { banner ->
                if (!banner.isDismissed) {
                    item(span = { GridItemSpan(maxLineSpan) }) {
                        AnnouncementBannerUI(
                            title = banner.title,
                            description = banner.body,
                            showReadMore = banner.showReadMore(),
                            onLearnMoreClick = { /*TODO*/ },
                            onDismissClick = onDismissClick
                        )
                    }
                }
            }

            uiState.loginInfoCardData?.let { data ->
                item(span = { GridItemSpan(maxLineSpan) }) {
                    LoginInfoCardUI(
                        onClick = onLoginClick,
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
                QuickAccessHeaderUI(
                    onManageClick = { /*TODO*/ },
                    onDismissTutorialClicked,
                    uiState = uiState
                )
            }

            items(uiState.quickAccessTileItems) {
                QuickAccessTileItemUI(
                    onClick = { },
                    icon = painterResource(id = it.icon),
                    title = it.name,
                    hasMoreOptions = it is QuickAccessTileItem.QuickLinkTileItem,
                    onMoreActionClick = {
                        if (it is QuickAccessTileItem.QuickLinkTileItem) {
                        }
                    }
                )
            }
        }
    }
}

@MultiDevicePreview
@Composable
private fun HomeScreenPreview(
    @PreviewParameter(HomeScreenUiStatePreviewProvider::class) uiState: HomeUiState
) {
    HealthGatewayTheme {
        HomeScreenContent(
            onDismissClick = {},
            onLoginClick = {},
            onDismissTutorialClicked = {},
            uiState = uiState
        )
    }
}

internal class HomeScreenUiStatePreviewProvider : PreviewParameterProvider<HomeUiState> {
    override val values: Sequence<HomeUiState> = sequenceOf(
        HomeUiState(
            loginInfoCardData = LoginInfoCardData(
                title = R.string.log_in_with_bc_services_card,
                description = R.string.log_in_description,
                buttonText = R.string.get_started,
                image = R.drawable.img_un_authenticated_home_screen
            )
        )
    )
}

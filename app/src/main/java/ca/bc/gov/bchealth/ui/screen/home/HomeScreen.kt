package ca.bc.gov.bchealth.ui.screen.home

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import ca.bc.gov.bchealth.Screen
import ca.bc.gov.bchealth.compose.MultiDevicePreview
import ca.bc.gov.bchealth.compose.component.AnnouncementBannerUI
import ca.bc.gov.bchealth.ui.auth.BioMetricState
import ca.bc.gov.bchealth.ui.auth.BiometricsAuthenticationFragment
import ca.bc.gov.bchealth.ui.home.LaunchCheckStatus

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

    HomeScreenContent(modifier, uiState)
}

@Composable
private fun HomeScreenContent(modifier: Modifier = Modifier, uiState: HomeUiState) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
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
                        onDismissClick = { }
                    )
                }
            }
        }
    }
}

@MultiDevicePreview
@Composable
private fun HomeScreenPreview() {
}

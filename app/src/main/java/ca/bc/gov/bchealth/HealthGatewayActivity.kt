package ca.bc.gov.bchealth

import android.app.Activity
import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.dialog
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navigation
import ca.bc.gov.bchealth.compose.theme.m3.HealthGatewayTheme
import ca.bc.gov.bchealth.ui.auth.BiometricSecurityTipDialogScreen
import ca.bc.gov.bchealth.ui.auth.BiometricSecurityTipScreen
import ca.bc.gov.bchealth.ui.auth.BiometricSecurityTipViewModel
import ca.bc.gov.bchealth.ui.auth.BiometricsAuthenticationScreen
import ca.bc.gov.bchealth.ui.inappupdate.InAppUpdateScreen
import ca.bc.gov.bchealth.ui.onboarding.OnBoardingScreen
import ca.bc.gov.bchealth.ui.onboarding.OnBoardingViewModel
import dagger.hilt.android.AndroidEntryPoint

/**
 * @author pinakin.kansara
 * Created 2023-10-12 at 1:22 p.m.
 */
@AndroidEntryPoint
class HealthGatewayActivity : AppCompatActivity() {

    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        setContent {

            val windowSizeClass = calculateWindowSizeClass(activity = this)
            val widthSizeClass = windowSizeClass.widthSizeClass
            val isExpanded = widthSizeClass != WindowWidthSizeClass.Compact
            // FIX orientation to portrait in condition if the device is MOBILE
            val activity = LocalContext.current as Activity
            if (widthSizeClass == WindowWidthSizeClass.Compact) {
                activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            } else {
                activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
            }

            HealthGatewayTheme {
                val navController = rememberNavController()
                val navBackStackEntry by navController.currentBackStackEntryAsState()

                when (navBackStackEntry?.destination?.route) {
                    Screen.InAppUpdate.route -> {
                    }
                }

                Scaffold(bottomBar = {
                    // BottomMenu(navController = navHostController)
                }, content = {
                    HealthGateWayApp(
                        modifier = Modifier
                            .statusBarsPadding()
                            .navigationBarsPadding()
                            .padding(it),
                        navHostController = navController,
                        isExpanded = isExpanded
                    )
                })
            }
        }
    }
}

@Composable
private fun HealthGateWayApp(
    modifier: Modifier = Modifier,
    navHostController: NavHostController,
    isExpanded: Boolean = false
) {

    NavHost(navController = navHostController, startDestination = Screen.OnBoarding.route) {
        composable(Screen.OnBoarding.route) {
            val viewModel = hiltViewModel<OnBoardingViewModel>()
            OnBoardingScreen(
                onGetStartedClick = { navHostController.navigate(Screen.BiometricAuthentication.route) },
                modifier = modifier,
                viewModel
            )
        }
        composable(Screen.InAppUpdate.route) {
            InAppUpdateScreen(
                onNavigate = { navHostController.navigate(Screen.OnBoarding.route) },
                modifier = modifier
            )
        }
        biometricGraph(modifier, navHostController, isExpanded)
    }
}

private fun NavGraphBuilder.biometricGraph(
    modifier: Modifier,
    navController: NavController,
    isExpanded: Boolean = false
) {
    navigation(Screen.BiometricAuthentication.route, route = "biometric") {
        composable(Screen.BiometricAuthentication.route) {
            BiometricsAuthenticationScreen(
                onLearnMoreClick = {
                    if (isExpanded) {
                        navController.navigate(Screen.BiometricSecurityTipDialog.route)
                    } else {
                        navController.navigate(Screen.BiometricSecurityTip.route)
                    }
                },
                modifier = modifier
            )
        }
        composable(Screen.BiometricSecurityTip.route) {
            val viewModel = hiltViewModel<BiometricSecurityTipViewModel>()
            BiometricSecurityTipScreen(onBackPress = {
                navController.popBackStack()
            }, viewModel = viewModel)
        }

        dialog(Screen.BiometricSecurityTipDialog.route) {
            val viewModel = hiltViewModel<BiometricSecurityTipViewModel>()
            BiometricSecurityTipDialogScreen(onDismiss = {
                navController.popBackStack()
            }, viewModel = viewModel)
        }
    }
}

@Composable
private fun BottomMenu(navController: NavController) {
    val items = listOf(
        BottomNavItem.Home,
        BottomNavItem.HealthRecord,
        BottomNavItem.Services,
        BottomNavItem.Dependent
    )
    var selectedItem by remember { mutableStateOf(0) }

    NavigationBar {
        items.forEachIndexed { index, item ->
            NavigationBarItem(
                icon = {
                    Icon(
                        painter = painterResource(id = item.icon),
                        contentDescription = item.route
                    )
                },
                label = { Text(text = stringResource(id = item.title)) },
                selected = selectedItem == index,
                onClick = { selectedItem = index }
            )
        }
    }
}

sealed class BottomNavItem(
    @StringRes val title: Int,
    @DrawableRes val icon: Int,
    val route: String
) {
    object Home : BottomNavItem(title = R.string.home, icon = R.drawable.ic_home, Screen.Home.route)
    object HealthRecord : BottomNavItem(
        title = R.string.records,
        icon = R.drawable.ic_home,
        Screen.HealthRecord.route
    )

    object Services : BottomNavItem(
        title = R.string.services,
        icon = R.drawable.ic_home,
        Screen.Services.route
    )

    object Dependent : BottomNavItem(
        title = R.string.dependent,
        icon = R.drawable.ic_home,
        Screen.Dependent.route
    )
}

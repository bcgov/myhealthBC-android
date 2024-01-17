package ca.bc.gov.bchealth

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.dialog
import androidx.navigation.navigation
import ca.bc.gov.bchealth.ui.auth.BiometricSecurityTipDialogScreen
import ca.bc.gov.bchealth.ui.auth.BiometricSecurityTipViewModel
import ca.bc.gov.bchealth.ui.auth.BiometricsAuthenticationFragment
import ca.bc.gov.bchealth.ui.screen.biometric.BiometricsAuthenticationScreen
import ca.bc.gov.bchealth.ui.screen.biometric.securitytip.BiometricSecurityTipScreen
import ca.bc.gov.bchealth.ui.screen.home.HomeScreen
import ca.bc.gov.bchealth.ui.screen.onboarding.OnBoardingScreen

/**
 * @author pinakin.kansara
 * Created 2023-12-11 at 4:07 p.m.
 */
@Composable
fun HealthGateWayNavHost(
    modifier: Modifier,
    navController: NavHostController,
    isExpanded: Boolean = false
) {

    NavHost(navController = navController, startDestination = "homeGraph") {

        composable(Screen.OnBoarding.route) {
            OnBoardingScreen(
                onGetStartedClick = { navController.popBackStack() },
                modifier = modifier
            )
        }

        biometricGraph(modifier, navController, isExpanded)

        homeGraph(modifier, navController, isExpanded)
        recordsGraph(modifier, navController, isExpanded)
        servicesGraph(modifier, navController, isExpanded)
        dependentGraph(modifier, navController, isExpanded)
    }
}

private fun NavGraphBuilder.homeGraph(
    modifier: Modifier,
    navController: NavController,
    isExpanded: Boolean
) {
    navigation(startDestination = Screen.Home.route, route = "homeGraph") {
        composable(Screen.Home.route) {
            HomeScreen(modifier = modifier, navController = navController)
        }
    }
}

private fun NavGraphBuilder.recordsGraph(
    modifier: Modifier,
    navController: NavController,
    isExpanded: Boolean
) {
    navigation(startDestination = Screen.HealthRecord.route, route = "healthRecordGraph") {
        composable(Screen.HealthRecord.route) {}
    }
}

private fun NavGraphBuilder.servicesGraph(
    modifier: Modifier,
    navController: NavController,
    isExpanded: Boolean
) {
    navigation(startDestination = Screen.Services.route, route = "servicesGraph") {
        composable(Screen.Services.route) {}
    }
}

private fun NavGraphBuilder.dependentGraph(
    modifier: Modifier,
    navController: NavController,
    isExpanded: Boolean
) {
    navigation(startDestination = Screen.Dependent.route, route = "dependentGraph") {
        composable(Screen.Dependent.route) {
        }
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
                onBiometricResult = {
                    navController.previousBackStackEntry?.savedStateHandle?.set(
                        BiometricsAuthenticationFragment.BIOMETRIC_STATE,
                        it
                    )
                    navController.popBackStack()
                },
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

package ca.bc.gov.bchealth

/**
 * @author pinakin.kansara
 * Created 2023-10-13 at 11:08 a.m.
 */
sealed class Screen(val route: String) {
    object OnBoarding : Screen("OnBoarding")
    object Home : Screen("Home")
    object HealthRecord : Screen("HealthRecord")
    object Services : Screen("Services")
    object Dependent : Screen("Dependent")
    object InAppUpdate : Screen("InAppUpdate")
    object BiometricAuthentication : Screen("BiometricAuthentication")
}

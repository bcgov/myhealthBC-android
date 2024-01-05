package ca.bc.gov.bchealth

import android.app.Activity
import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import ca.bc.gov.bchealth.compose.theme.m3.HealthGatewayTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

/**
 * @author pinakin.kansara
 * Created 2023-10-12 at 1:22 p.m.
 */
@AndroidEntryPoint
class HealthGatewayActivity : AppCompatActivity() {

    private val viewModel: HealthGateWayViewModel by viewModels()
    private var keepScreenOn: Boolean = true

    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)

        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    keepScreenOn = state.keepSplashScreen
                }
            }
        }

        splashScreen.setKeepOnScreenCondition {
            keepScreenOn
        }

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
                HealthGateWayApp(
                    isExpanded = isExpanded
                )
            }
        }
    }
}

@Composable
fun BottomMenu(navController: NavController) {
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
                onClick = {
                    selectedItem = index
                    navController.navigate(item.route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
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

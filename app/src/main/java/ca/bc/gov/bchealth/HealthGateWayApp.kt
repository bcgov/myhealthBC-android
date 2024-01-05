package ca.bc.gov.bchealth

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController

/**
 * @author pinakin.kansara
 * Created 2023-12-11 at 3:35 p.m.
 */
@Composable
fun HealthGateWayApp(
    isExpanded: Boolean = false
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    Log.d("Navigation", "destination = ${navBackStackEntry?.destination?.route}")

    val showBottomBar by remember {
        derivedStateOf {
            when (navBackStackEntry?.destination?.route) {
                Screen.Home.route,
                Screen.HealthRecord.route,
                Screen.Services.route,
                Screen.Dependent.route -> {
                    true
                }

                else -> {
                    false
                }
            }
        }
    }

    Scaffold(bottomBar = {
        AnimatedVisibility(visible = showBottomBar) {
            BottomMenu(navController = navController)
        }
    }, content = {
        HealthGateWayNavHost(
            modifier = Modifier
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(it),
            navController, isExpanded
        )
    })
}

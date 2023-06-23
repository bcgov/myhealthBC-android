package ca.bc.gov.bchealth.ui.component

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.SwipeRefreshIndicator
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState

@Composable
fun SwipeToRefreshUI(modifier: Modifier = Modifier, onRefresh: () -> Unit, content: @Composable () -> Unit) {
    val swipeRefreshState = rememberSwipeRefreshState(false)
    SwipeRefresh(modifier = modifier.fillMaxSize(), state = swipeRefreshState, onRefresh = {
        swipeRefreshState.isRefreshing = false
        onRefresh()
    }, indicator = { state, trigger ->
        SwipeRefreshIndicator(
            state = state,
            refreshTriggerDistance = trigger,
            // Enable the scale animation
            scale = true,
            // Change the color and shape
            contentColor = MaterialTheme.colors.primary,
            shape = MaterialTheme.shapes.small,
        )
    }) {
        content()
    }
}

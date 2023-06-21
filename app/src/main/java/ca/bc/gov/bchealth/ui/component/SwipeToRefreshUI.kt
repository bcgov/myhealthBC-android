package ca.bc.gov.bchealth.ui.component

import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.SwipeRefreshIndicator
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState

@Composable
fun SwipeToRefreshUI(onRefresh: () -> Unit, content: @Composable () -> Unit) {
    val swipeRefreshState = rememberSwipeRefreshState(false)
    SwipeRefresh(state = swipeRefreshState, onRefresh = {
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

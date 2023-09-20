package ca.bc.gov.bchealth.ui.recommendations

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.work.WorkManager
import ca.bc.gov.bchealth.R
import ca.bc.gov.bchealth.compose.BasePreview
import ca.bc.gov.bchealth.compose.component.HGCircularProgressIndicator
import ca.bc.gov.bchealth.compose.theme.HealthGatewayTheme
import ca.bc.gov.bchealth.compose.theme.grey
import ca.bc.gov.bchealth.ui.custom.MyHealthClickableText
import ca.bc.gov.repository.bcsc.BACKGROUND_AUTH_RECORD_FETCH_WORK_NAME

@Composable
fun RecommendationScreen(
    modifier: Modifier = Modifier,
    viewModel: RecommendationsViewModel,
    onLinkClicked: () -> Unit
) {

    val uiState =
        viewModel.uiState.collectAsStateWithLifecycle(minActiveState = Lifecycle.State.RESUMED).value
    val context = LocalContext.current
    val workRequest = WorkManager.getInstance(context)
        .getWorkInfosForUniqueWorkLiveData(BACKGROUND_AUTH_RECORD_FETCH_WORK_NAME)
        .observeAsState()
    val workState = workRequest.value?.firstOrNull()?.state
    if (workState != null && workState.isFinished) {
        LaunchedEffect(key1 = Unit) {
            viewModel.loadRecommendations()
        }
    } else {
        LaunchedEffect(Unit) {
            viewModel.showProgress()
        }
    }

    if (uiState.isLoading) {
        HGCircularProgressIndicator(modifier)
    } else {
        RecommendationScreenContent(
            modifier,
            uiState.patientWithRecommendations,
            expandedIds = uiState.expandedCardIds,
            onArrowClick = {
                viewModel.expandedCard(it)
            },
            onLinkClicked = onLinkClicked
        )
    }
}

@Composable
private fun RecommendationScreenContent(
    modifier: Modifier = Modifier,
    recommendations: List<PatientWithRecommendations>,
    expandedIds: Set<Long>,
    onArrowClick: (Long) -> Unit,
    onLinkClicked: () -> Unit
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(32.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        item {
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = stringResource(id = R.string.recommendations_para1),
                style = MaterialTheme.typography.body2,
                color = grey
            )
        }
        item {

            MyHealthClickableText(
                style = MaterialTheme.typography.body2.copy(color = grey),
                modifier = Modifier.fillMaxWidth(),
                fullText = stringResource(id = R.string.recommendations_para2),
                clickableText = stringResource(id = R.string.immunizeBC),
                action = onLinkClicked
            )
        }

        items(
            recommendations,
            key = { item -> item.patientId }
        ) {
            RecommendationItem(
                patientWithRecommendations = it,
                expanded = expandedIds.contains(it.patientId),
                onArrowClick = { onArrowClick(it.patientId) }
            )
        }
    }
}

@BasePreview
@Composable
private fun RecommendationScreenPreview() {

    HealthGatewayTheme {
        RecommendationScreenContent(
            recommendations = emptyList(),
            expandedIds = emptySet(),
            onArrowClick = {},
            onLinkClicked = {}
        )
    }
}

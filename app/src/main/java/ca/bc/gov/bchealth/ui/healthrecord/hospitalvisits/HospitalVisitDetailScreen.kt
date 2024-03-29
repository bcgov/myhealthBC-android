package ca.bc.gov.bchealth.ui.healthrecord.hospitalvisits

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.work.WorkManager
import ca.bc.gov.bchealth.R
import ca.bc.gov.bchealth.compose.BasePreview
import ca.bc.gov.bchealth.ui.comment.CommentEntryTypeCode
import ca.bc.gov.bchealth.ui.comment.CommentsSummary
import ca.bc.gov.bchealth.ui.comment.CommentsSummaryUI
import ca.bc.gov.bchealth.ui.comment.CommentsUiState
import ca.bc.gov.bchealth.ui.comment.CommentsViewModel
import ca.bc.gov.bchealth.ui.healthrecord.HealthRecordDetailItem
import ca.bc.gov.bchealth.ui.healthrecord.HealthRecordListItem
import ca.bc.gov.bchealth.widget.CommentInputUI
import ca.bc.gov.common.BuildConfig
import ca.bc.gov.repository.SYNC_COMMENTS

@Composable
fun HospitalVisitDetailScreen(
    onClickComments: (CommentsSummary) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: HospitalVisitDetailViewModel,
    commentsViModel: CommentsViewModel,
    id: Long
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val commentsState by commentsViModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(key1 = Unit) {
        viewModel.getHospitalVisitDetails(id)
    }

    LaunchedEffect(key1 = uiState.id) {
        uiState.id?.let {
            commentsViModel.getComments(it)
        }
    }

    val context = LocalContext.current
    val workRequest = WorkManager.getInstance(context)
        .getWorkInfosForUniqueWorkLiveData(SYNC_COMMENTS)
        .observeAsState()
    val workState = workRequest.value?.firstOrNull()?.state
    if (workState != null && workState.isFinished) {
        LaunchedEffect(key1 = uiState.id) {
            uiState.id?.let {
                commentsViModel.getComments(it)
            }
        }
    }

    HospitalVisitDetailScreenContent(
        onClickComments = onClickComments,
        onSubmitComment = { comment ->
            uiState.id?.let {
                commentsViModel.addComment(it, comment, CommentEntryTypeCode.HOSPITAL_VISIT.value)
            }
        },
        modifier,
        uiState = uiState,
        commentsState
    )
}

@Composable
private fun HospitalVisitDetailScreenContent(
    onClickComments: (CommentsSummary) -> Unit,
    onSubmitComment: (String) -> Unit,
    modifier: Modifier = Modifier,
    uiState: HospitalVisitUiState,
    commentsUiState: CommentsUiState
) {

    Column(modifier = modifier.fillMaxSize()) {
        LazyColumn(
            modifier.weight(1F), contentPadding = PaddingValues(32.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(uiState.uiList) { item ->
                HealthRecordListItem(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight(),
                    label = stringResource(id = item.title),
                    value = item.placeholder?.let {
                        stringResource(id = it)
                    } ?: item.description.orEmpty(),
                    footer = item.footer?.let { stringResource(it) }
                )
            }
        }
        if (BuildConfig.FLAG_ADD_COMMENTS) {
            CommentsSummaryUI(
                onClickComments = onClickComments,
                commentsSummary = commentsUiState.commentsSummary
            )
        }

        if (BuildConfig.FLAG_ADD_COMMENTS) {
            CommentInputUI(onSubmitComment = onSubmitComment)
        }
    }
}

@BasePreview
@Composable
fun PreviewHospitalVisitListItem() {
    val list = listOf(
        HealthRecordDetailItem(
            R.string.hospital_visits_detail_location_title,
            "Vancouver General Hospital",
            footer = R.string.hospital_visits_detail_location_title,
        ),
        HealthRecordDetailItem(
            R.string.hospital_visits_detail_location_title,
            "Vancouver General Hospital",
        ),
        HealthRecordDetailItem(
            R.string.hospital_visits_detail_location_title,
            "Vancouver General Hospital",
            placeholder = R.string.place_holder_text,
        )
    )
}

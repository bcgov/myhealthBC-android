package ca.bc.gov.bchealth.ui.healthrecord.imaging

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.work.WorkManager
import ca.bc.gov.bchealth.R
import ca.bc.gov.bchealth.compose.BasePreview
import ca.bc.gov.bchealth.compose.MyHealthTypography
import ca.bc.gov.bchealth.compose.theme.descriptionGrey
import ca.bc.gov.bchealth.ui.comment.CommentEntryTypeCode
import ca.bc.gov.bchealth.ui.comment.CommentsSummary
import ca.bc.gov.bchealth.ui.comment.CommentsSummaryUI
import ca.bc.gov.bchealth.ui.comment.CommentsUiState
import ca.bc.gov.bchealth.ui.comment.CommentsViewModel
import ca.bc.gov.bchealth.ui.component.HGLargeOutlinedButton
import ca.bc.gov.bchealth.ui.healthrecord.HealthRecordListItem
import ca.bc.gov.bchealth.widget.CommentInputUI
import ca.bc.gov.common.BuildConfig
import ca.bc.gov.repository.SYNC_COMMENTS

@Composable
fun DiagnosticImagingDetailScreen(
    onClickComments: (CommentsSummary) -> Unit,
    modifier: Modifier,
    viewModel: DiagnosticImagingDetailViewModel,
    commentsViewModel: CommentsViewModel,
    id: Long
) {

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val commentsUiState by commentsViewModel.uiState.collectAsStateWithLifecycle()

    val context = LocalContext.current
    val workRequest = WorkManager.getInstance(context)
        .getWorkInfosForUniqueWorkLiveData(SYNC_COMMENTS)
        .observeAsState()
    val workState = workRequest.value?.firstOrNull()?.state
    if (workState != null && workState.isFinished) {
        LaunchedEffect(key1 = Unit) {
            uiState.id?.let {
                commentsViewModel.getComments(it)
            }
        }
    }

    LaunchedEffect(key1 = Unit) {
        viewModel.getDiagnosticImagingDataDetails(id)
    }

    DiagnosticImagingDetailContent(
        onClickDownload = viewModel::onClickDownload,
        onClickComments = onClickComments,
        onSubmitComment = { comment ->
            uiState.id?.let {
                commentsViewModel.addComment(it, comment, CommentEntryTypeCode.DIAGNOSTIC_IMAGING.value)
            }
        },
        modifier,
        uiState,
        commentsUiState
    )
}

@Composable
private fun DiagnosticImagingDetailContent(
    onClickDownload: () -> Unit,
    onClickComments: (CommentsSummary) -> Unit,
    onSubmitComment: (String) -> Unit,
    modifier: Modifier = Modifier,
    uiState: DiagnosticImagingDataDetailUiState,
    commentsUiState: CommentsUiState
) {

    Box(
        modifier = modifier
            .fillMaxSize()
    ) {

        if (uiState.onLoading) {
            CircularProgressIndicator(
                modifier = modifier.align(Alignment.Center),
                color = MaterialTheme.colors.primary
            )
        }
        Column(modifier = modifier.fillMaxSize()) {
            LazyColumn(
                modifier.weight(1F), contentPadding = PaddingValues(32.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                if (!uiState.fileId.isNullOrBlank()) {
                    item {
                        HGLargeOutlinedButton(
                            onClick = onClickDownload,
                            modifier = Modifier.fillMaxWidth(),
                            text = stringResource(id = R.string.clinical_documents_detail_button_download)
                        )
                    }
                }

                item {
                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 24.dp),
                        text = stringResource(id = R.string.diagnostic_imaging_detail_subtitle),
                        style = MyHealthTypography.h4,
                        color = descriptionGrey
                    )
                }

                items(uiState.details) {
                    HealthRecordListItem(
                        modifier = Modifier.padding(top = 24.dp),
                        stringResource(id = it.title),
                        it.description.orEmpty(),
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
}

@Composable
@BasePreview
private fun DiagnosticImagingDetailScreenPreview() {
}

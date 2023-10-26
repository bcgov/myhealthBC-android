package ca.bc.gov.bchealth.ui.healthrecord.clinicaldocument

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
import ca.bc.gov.bchealth.ui.component.HGLargeOutlinedButton
import ca.bc.gov.bchealth.ui.healthrecord.HealthRecordListItem
import ca.bc.gov.bchealth.widget.CommentInputUI
import ca.bc.gov.common.BuildConfig
import ca.bc.gov.repository.SYNC_COMMENTS

@Composable
fun ClinicalDocumentDetailScreen(
    onClickComments: (CommentsSummary) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ClinicalDocumentDetailViewModel,
    commentsViModel: CommentsViewModel,
    documentId: Long,
    hdid: String?
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val commentsUiState by commentsViModel.uiState.collectAsStateWithLifecycle()

    val context = LocalContext.current
    val workRequest = WorkManager.getInstance(context)
        .getWorkInfosForUniqueWorkLiveData(SYNC_COMMENTS)
        .observeAsState()
    val workState = workRequest.value?.firstOrNull()?.state
    if (workState != null && workState.isFinished) {
        LaunchedEffect(key1 = Unit) {
            uiState.id?.let {
                commentsViModel.getComments(it)
            }
        }
    }

    LaunchedEffect(key1 = Unit) {
        viewModel.getClinicalDocumentDetails(documentId, hdid)
    }

    ClinicalDocumentDetailScreenContent(
        onClickDownload = viewModel::onClickDownload,
        onClickComments = onClickComments,
        onSubmitComment = { comment ->
            uiState.id?.let {
                commentsViModel.addComment(it, comment, CommentEntryTypeCode.CLINICAL_DOCS.value)
            }
        },
        modifier,
        uiState,
        commentsUiState
    )
}

@Composable
private fun ClinicalDocumentDetailScreenContent(
    onClickDownload: () -> Unit,
    onClickComments: (CommentsSummary) -> Unit,
    onSubmitComment: (String) -> Unit,
    modifier: Modifier = Modifier,
    uiState: ClinicalDocumentUiState,
    commentUIState: CommentsUiState
) {

    Column(modifier = modifier.fillMaxSize()) {
        LazyColumn(
            modifier.weight(1F), contentPadding = PaddingValues(32.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {

                HGLargeOutlinedButton(
                    onClick = onClickDownload,
                    modifier = Modifier
                        .fillMaxWidth(),
                    text = stringResource(id = R.string.clinical_documents_detail_button_download)
                )
            }

            items(uiState.uiList) { listItem ->
                HealthRecordListItem(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight(),
                    stringResource(id = listItem.title),
                    listItem.description.orEmpty(),
                )
            }
        }

        if (BuildConfig.FLAG_ADD_COMMENTS) {
            CommentsSummaryUI(
                onClickComments = onClickComments,
                commentsSummary = commentUIState.commentsSummary
            )
        }

        if (BuildConfig.FLAG_ADD_COMMENTS) {
            CommentInputUI(onSubmitComment = onSubmitComment)
        }
    }
}

@BasePreview
@Composable
private fun PreviewClinicalDocumentDetailContent() {
}

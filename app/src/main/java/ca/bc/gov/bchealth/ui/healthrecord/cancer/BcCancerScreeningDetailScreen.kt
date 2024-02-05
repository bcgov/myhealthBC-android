package ca.bc.gov.bchealth.ui.healthrecord.cancer

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.work.WorkManager
import ca.bc.gov.bchealth.compose.BasePreview
import ca.bc.gov.bchealth.compose.MyHealthTypography
import ca.bc.gov.bchealth.ui.comment.CommentEntryTypeCode
import ca.bc.gov.bchealth.ui.comment.CommentsSummary
import ca.bc.gov.bchealth.ui.comment.CommentsSummaryUI
import ca.bc.gov.bchealth.ui.comment.CommentsUiState
import ca.bc.gov.bchealth.ui.comment.CommentsViewModel
import ca.bc.gov.bchealth.ui.component.HGLargeOutlinedButton
import ca.bc.gov.bchealth.ui.custom.MyHealthClickableText
import ca.bc.gov.bchealth.widget.CommentInputUI
import ca.bc.gov.common.BuildConfig
import ca.bc.gov.repository.SYNC_COMMENTS

/**
 * @author pinakin.kansara
 * Created 2024-01-19 at 11:40 a.m.
 */
@Composable
fun BcCancerScreeningDetailScreen(
    onClickComments: (CommentsSummary) -> Unit,
    onClickLink: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: BcCancerScreeningDetailViewModel,
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
        LaunchedEffect(key1 = uiState.id) {
            uiState.id?.let {
                commentsViewModel.getComments(it)
            }
        }
    }

    LaunchedEffect(key1 = Unit) {
        viewModel.getBcCancerScreeningData(id)
    }

    LaunchedEffect(key1 = uiState.id) {
        uiState.id?.let {
            commentsViewModel.getComments(it)
        }
    }

    BcCancerScreeningDetailScreenContent(
        onClickDownload = viewModel::onClickDownload,
        onClickComments = onClickComments,
        onClickLink = onClickLink,
        onSubmitComment = { comment ->
            uiState.id?.let {
                commentsViewModel.addComment(
                    it,
                    comment,
                    CommentEntryTypeCode.DIAGNOSTIC_IMAGING.value
                )
            }
        }, modifier = modifier, uiState = uiState, commentsUiState = commentsUiState
    )
}

@Composable
private fun BcCancerScreeningDetailScreenContent(
    onClickDownload: () -> Unit,
    onClickComments: (CommentsSummary) -> Unit,
    onSubmitComment: (String) -> Unit,
    onClickLink: (String) -> Unit,
    modifier: Modifier = Modifier,
    uiState: BcCancerScreeningDataDetailUiState,
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
            Column(
                modifier
                    .weight(1F)
                    .fillMaxWidth()
                    .padding(32.dp)
            ) {

                if (!uiState.fileId.isNullOrBlank()) {
                    HGLargeOutlinedButton(
                        onClick = onClickDownload,
                        modifier = Modifier.fillMaxWidth(),
                        text = uiState.pdfButtonTitle
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))
                MyHealthClickableText(
                    modifier = Modifier
                        .fillMaxWidth(),
                    style = MyHealthTypography.h4.copy(textAlign = TextAlign.Start),
                    fullText = stringResource(uiState.description),
                    clickableText = uiState.links?.name ?: "",
                    action = { onClickLink(uiState.links?.link ?: "") }
                )
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

@BasePreview
@Composable
private fun BcCancerScreeningDetailScreenPreview() {
}

package ca.bc.gov.bchealth.ui.healthrecord.healthvisits

import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import ca.bc.gov.bchealth.R
import ca.bc.gov.bchealth.compose.MyHealthTypography
import ca.bc.gov.bchealth.ui.comment.CommentsSummary
import ca.bc.gov.bchealth.ui.comment.CommentsSummaryUI
import ca.bc.gov.bchealth.ui.comment.CommentsViewModel
import ca.bc.gov.bchealth.ui.custom.MyHealthClickableText
import ca.bc.gov.bchealth.ui.healthrecord.HealthRecordListItem
import ca.bc.gov.bchealth.widget.CommentInputUI
import ca.bc.gov.common.BuildConfig

@Composable
fun BoxScope.HealthVisitDetailScreen(
    uiState: HealthVisitDetailUiState,
    onClickFaq: () -> Unit,
    onClickComments: (CommentsSummary) -> Unit,
    commentsViewModel: CommentsViewModel,
    onSubmitComment: (String) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .align(Alignment.TopCenter),
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
        ) {
            MyHealthClickableText(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp, start = 32.dp, end = 32.dp),
                style = MyHealthTypography.caption,
                fullText = stringResource(id = R.string.information_is_from_the_billing_claim),
                clickableText = stringResource(id = R.string.faq),
                action = onClickFaq
            )

            uiState.uiList.forEach { listItem ->
                HealthRecordListItem(
                    stringResource(id = listItem.title),
                    listItem.description.orEmpty(),
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            if (BuildConfig.FLAG_ADD_COMMENTS) {
                CommentsSummaryUI(
                    commentsViewModel = commentsViewModel,
                    onClickComments = onClickComments
                )
            }
        }

        if (BuildConfig.FLAG_ADD_COMMENTS) {
            CommentInputUI(onSubmitComment = onSubmitComment)
        }
    }
}

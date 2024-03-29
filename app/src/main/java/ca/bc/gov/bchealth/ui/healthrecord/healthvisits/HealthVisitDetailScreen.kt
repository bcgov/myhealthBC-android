package ca.bc.gov.bchealth.ui.healthrecord.healthvisits

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import ca.bc.gov.bchealth.R
import ca.bc.gov.bchealth.compose.BasePreview
import ca.bc.gov.bchealth.compose.MyHealthTypography
import ca.bc.gov.bchealth.ui.comment.CommentsSummary
import ca.bc.gov.bchealth.ui.comment.CommentsSummaryUI
import ca.bc.gov.bchealth.ui.custom.MyHealthClickableText
import ca.bc.gov.bchealth.ui.healthrecord.HealthRecordDetailItem
import ca.bc.gov.bchealth.ui.healthrecord.HealthRecordListItem
import ca.bc.gov.bchealth.widget.CommentInputUI
import ca.bc.gov.common.BuildConfig
import ca.bc.gov.common.model.SyncStatus

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun BoxScope.HealthVisitDetailScreen(
    uiState: HealthVisitDetailUiState,
    onClickFaq: () -> Unit,
    onClickComments: (CommentsSummary) -> Unit,
    commentsSummary: CommentsSummary?,
    onSubmitComment: (String) -> Unit,
) {

    val keyboardController = LocalSoftwareKeyboardController.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTapGestures(onPress = { keyboardController?.hide() })
            }
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
                clickableText = stringResource(id = R.string.faq_clickable),
                action = onClickFaq
            )

            uiState.uiList.forEach { listItem ->
                HealthRecordListItem(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .padding(top = 16.dp, start = 32.dp, end = 32.dp),
                    stringResource(id = listItem.title),
                    listItem.description.orEmpty(),
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            if (BuildConfig.FLAG_ADD_COMMENTS) {
                CommentsSummaryUI(
                    commentsSummary = commentsSummary,
                    onClickComments = onClickComments
                )
            }
        }

        if (BuildConfig.FLAG_ADD_COMMENTS) {
            CommentInputUI(onSubmitComment = onSubmitComment)
        }
    }
}

@BasePreview
@Composable
private fun PreviewHealthVisitDetailContent() {
    Box {
        HealthVisitDetailScreen(
            HealthVisitDetailUiState(
                uiList = listOf(
                    HealthRecordDetailItem(
                        R.string.clinic_name,
                        "FRANCIS N WER"
                    ),
                    HealthRecordDetailItem(
                        R.string.practitioner_name,
                        "Daniel Something"
                    )
                )
            ),
            {}, {},
            CommentsSummary(
                text = "comment05",
                date = null,
                entryTypeCode = "",
                count = 5,
                parentEntryId = "",
                syncStatus = SyncStatus.UP_TO_DATE
            ),
            {}
        )
    }
}

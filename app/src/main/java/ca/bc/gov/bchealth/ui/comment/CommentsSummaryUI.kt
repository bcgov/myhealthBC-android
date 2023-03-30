package ca.bc.gov.bchealth.ui.comment

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.unit.dp
import ca.bc.gov.bchealth.R
import ca.bc.gov.bchealth.compose.BasePreview
import ca.bc.gov.bchealth.compose.MyHealthTheme
import ca.bc.gov.bchealth.compose.MyHealthTypography
import ca.bc.gov.bchealth.compose.blue
import ca.bc.gov.bchealth.compose.grey
import ca.bc.gov.bchealth.compose.greyBg
import ca.bc.gov.common.utils.toDateTimeString
import java.time.Instant

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun CommentsSummaryUI(count: Int, lastComment: Comment) {
    Column(Modifier.padding(horizontal = 32.dp, vertical = 4.dp)) {
        Text(
            text = pluralStringResource(id = R.plurals.plurals_comments, count = count, count),
            style = MyHealthTypography.h3.copy(color = blue)
        )
        CommentItem(lastComment)
    }
}

@Composable
private fun CommentItem(comment: Comment) {
    val commentMsg = comment.text.orEmpty()
    val footer = comment.date?.toDateTimeString().orEmpty()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp)
            .clip(RoundedCornerShape(4.dp))
            .background(greyBg)
    ) {

        Text(
            modifier = Modifier.padding(top = 8.dp, start = 8.dp, end = 8.dp),
            text = commentMsg,
            style = MyHealthTypography.body2
        )

        Text(
            text = footer,
            style = MyHealthTypography.caption.copy(color = grey),
            modifier = Modifier.padding(bottom = 8.dp, start = 8.dp, end = 8.dp)
        )
    }
}

@Composable
@BasePreview
fun PreviewCommentsSummaryUI() {
    val date = Instant.now()
    MyHealthTheme {
        val comment = Comment(
            text = "comment05",
            date = date,
            version = 0L,
            entryTypeCode = "",
            createdBy = "",
            createdDateTime = date,
            updatedDateTime = date,
            updatedBy = ""
        )

        CommentsSummaryUI(5, comment)
    }
}

package ca.bc.gov.bchealth.ui.comment

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import ca.bc.gov.bchealth.R
import ca.bc.gov.bchealth.compose.BasePreview
import ca.bc.gov.bchealth.compose.MyHealthTypography
import ca.bc.gov.bchealth.compose.grey
import ca.bc.gov.bchealth.compose.greyBg
import ca.bc.gov.bchealth.compose.minButtonSize
import ca.bc.gov.bchealth.ui.custom.MyHealthScaffold
import ca.bc.gov.bchealth.widget.CommentInputUI
import ca.bc.gov.common.utils.toDateTimeString
import java.time.Instant

@Composable
fun CommentsUI(
    navigationAction: () -> Unit,
    viewModel: CommentsViewModel,
    editAction: (Comment) -> Unit,
    submitAction: (String) -> Unit,
) {
    val uiState = viewModel.uiState.collectAsState().value

    MyHealthScaffold(
        title = stringResource(id = R.string.comments),
        isLoading = uiState.onLoading,
        navigationAction = navigationAction
    ) {
        CommentsContent(uiState.commentsList, editAction, submitAction)
    }
}

@Composable
fun CommentsContent(
    comments: List<Comment>,
    editAction: (Comment) -> Unit,
    submitAction: (String) -> Unit,
) {
    Column(Modifier.imePadding()) {
        Text(
            modifier = Modifier.padding(top = 16.dp, start = 32.dp, end = 32.dp),
            text = stringResource(id = R.string.comments_medication_subtitle)
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(top = 8.dp)
        ) {
            items(comments) { comment ->
                CommentItemUI(comment, editAction)
            }
        }

        CommentInputUI(onSubmitComment = submitAction)
    }
}

@Composable
fun CommentItemUI(comment: Comment, editAction: (Comment) -> Unit) {
    val commentMsg = comment.text.orEmpty()
    val footer = if (comment.isUploaded) {
        comment.date?.toDateTimeString().orEmpty()
    } else {
        stringResource(R.string.posting)
    }

    Column(
        modifier = Modifier
            .padding(horizontal = 32.dp, vertical = 8.dp)
            .clip(RoundedCornerShape(4.dp))
            .background(greyBg)
    ) {
        Row(modifier = Modifier.fillMaxWidth()) {
            Text(
                modifier = Modifier
                    .weight(1f)
                    .padding(top = 8.dp, start = 8.dp),
                text = commentMsg,
                style = MyHealthTypography.body2
            )
            Image(
                painter = painterResource(id = R.drawable.ic_comment_options),
                contentScale = ContentScale.Inside,
                modifier = Modifier
                    .width(minButtonSize)
                    .height(minButtonSize)
                    .clickable { editAction.invoke(comment) },
                contentDescription = stringResource(id = R.string.edit)
            )
        }

        Text(
            text = footer,
            style = MyHealthTypography.caption.copy(color = grey),
            modifier = Modifier.padding(bottom = 8.dp, start = 8.dp, end = 8.dp)
        )
    }
}

@BasePreview
@Composable
fun PreviewCommentsContent() {
    val date = Instant.now()
    val comments = listOf(
        Comment(text = "comment01", date = date, isUploaded = true),
        Comment(text = "comment02", date = date, isUploaded = false),
    )
    CommentsContent(comments, {}) {}
}

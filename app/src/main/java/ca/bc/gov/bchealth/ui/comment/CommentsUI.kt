package ca.bc.gov.bchealth.ui.comment

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
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
import ca.bc.gov.bchealth.compose.primaryBlue
import ca.bc.gov.bchealth.compose.red
import ca.bc.gov.bchealth.ui.custom.MyHealthScaffold
import ca.bc.gov.bchealth.widget.CommentInputUI
import ca.bc.gov.common.utils.toDateTimeString
import kotlinx.coroutines.flow.StateFlow
import java.time.Instant

@Composable
fun CommentsUI(
    uiStateFlow: StateFlow<CommentsUiState>,
    navigationAction: () -> Unit,
    editAction: (Comment) -> Unit,
    deleteAction: (Comment) -> Unit,
    submitAction: (String) -> Unit,
    updateAction: (Comment) -> Unit,
    cancelAction: (Comment) -> Unit,
) {
    val uiState = uiStateFlow.collectAsState().value

    MyHealthScaffold(
        title = stringResource(id = R.string.comments),
        isLoading = uiState.onLoading,
        navigationAction = navigationAction
    ) {
        CommentsContent(
            uiState,
            editAction,
            deleteAction,
            submitAction,
            updateAction,
            cancelAction,
        )
    }
}

@Composable
fun CommentsContent(
    uiState: CommentsUiState,
    editAction: (Comment) -> Unit,
    deleteAction: (Comment) -> Unit,
    submitAction: (String) -> Unit,
    updateAction: (Comment) -> Unit,
    cancelAction: (Comment) -> Unit,
) {
    val comments = uiState.commentsList

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
                CommentItemUI(
                    uiState.displayEditLayout,
                    comment,
                    editAction,
                    deleteAction,
                    updateAction,
                    cancelAction
                )
            }
        }

        if (uiState.displayEditLayout.not()) {
            CommentInputUI(onSubmitComment = submitAction)
        }
    }
}

@Composable
private fun CommentItemUI(
    displayEditLayout: Boolean,
    comment: Comment,
    editAction: (Comment) -> Unit,
    deleteAction: (Comment) -> Unit,
    updateAction: (Comment) -> Unit,
    cancelAction: (Comment) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }

    val commentMsg = comment.text.orEmpty()
    val footer = if (comment.isUploaded) {
        comment.date?.toDateTimeString().orEmpty()
    } else {
        stringResource(R.string.posting)
    }

    val alpha = if (displayEditLayout) 0.3f else 1f

    if (comment.editable) {
        CommentInputUI(comment, updateAction, cancelAction)
    } else {
        Column(
            modifier = Modifier
                .padding(horizontal = 32.dp, vertical = 8.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(greyBg)
                .alpha(alpha)
        ) {
            Row(modifier = Modifier.fillMaxWidth()) {
                Text(
                    modifier = Modifier
                        .weight(1f)
                        .padding(top = 8.dp, start = 8.dp),
                    text = commentMsg,
                    style = MyHealthTypography.body2
                )

                Box(modifier = Modifier.wrapContentSize(Alignment.TopStart)) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_comment_options),
                        contentScale = ContentScale.Inside,
                        modifier = Modifier
                            .width(minButtonSize)
                            .height(minButtonSize)
                            .clickable {
                                if (displayEditLayout.not() && comment.isUploaded) {
                                    expanded = true
                                }
                            },
                        contentDescription = stringResource(id = R.string.edit)
                    )

                    OptionsMenu(
                        expanded = expanded,
                        onDismissMenu = { expanded = false },
                        onClickDelete = {
                            deleteAction.invoke(comment)
                            expanded = false
                        },
                        onClickEdit = {
                            editAction.invoke(comment)
                            expanded = false
                        }
                    )
                }
            }

            Text(
                text = footer,
                style = MyHealthTypography.caption.copy(color = grey),
                modifier = Modifier.padding(bottom = 8.dp, start = 8.dp, end = 8.dp)
            )
        }
    }
}

@Composable
private fun OptionsMenu(
    expanded: Boolean,
    onDismissMenu: () -> Unit,
    onClickEdit: () -> Unit,
    onClickDelete: () -> Unit,
) {
    DropdownMenu(
        expanded = expanded,
        onDismissRequest = onDismissMenu
    ) {
        DropdownMenuItem(onClick = onClickEdit) {
            Text(
                text = stringResource(id = R.string.comment_edit),
                style = MyHealthTypography.body2.copy(color = primaryBlue)
            )
        }

        Divider(modifier = Modifier.padding(vertical = 6.dp))

        DropdownMenuItem(onClick = onClickDelete) {
            Text(
                text = stringResource(id = R.string.comment_delete),
                style = MyHealthTypography.body2.copy(color = red)
            )
        }
    }
}

@BasePreview
@Composable
fun PreviewCommentsContent() {
    val date = Instant.now()
    val comment = Comment(
        text = "comment01",
        date = date,
        version = 0L,
        isUploaded = true,
        entryTypeCode = "",
        createdBy = "",
        createdDateTime = date,
        updatedDateTime = date,
        updatedBy = ""
    )

    val comments = listOf(
        comment,
        comment.copy(text = "comment02", isUploaded = false),
    )

    val uiState = CommentsUiState(
        commentsList = comments
    )
    CommentsContent(uiState, {}, {}, {}, {}, {})
}

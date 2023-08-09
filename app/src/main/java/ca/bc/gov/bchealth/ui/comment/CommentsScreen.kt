package ca.bc.gov.bchealth.ui.comment

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
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
import ca.bc.gov.bchealth.widget.CommentInputUI
import ca.bc.gov.bchealth.widget.EditableCommentInputUI
import ca.bc.gov.common.model.SyncStatus
import ca.bc.gov.common.utils.toDateTimeString
import kotlinx.coroutines.launch
import java.time.Instant

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun CommentsScreen(
    uiState: CommentsUiState,
    editAction: (Comment) -> Unit,
    deleteAction: (Comment) -> Unit,
    submitAction: (String) -> Unit,
    updateAction: (Comment) -> Unit,
    cancelAction: (Comment) -> Unit,
) {
    val comments = uiState.commentsList ?: return
    val keyboardController = LocalSoftwareKeyboardController.current
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .imePadding()
            .pointerInput(Unit) {
                detectTapGestures(onPress = { keyboardController?.hide() })
            }
    ) {
        Text(
            modifier = Modifier.padding(top = 16.dp, start = 32.dp, end = 32.dp),
            text = stringResource(id = R.string.comments_list_subtitle)
        )

        LazyColumn(
            state = listState,
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
            CommentInputUI(onSubmitComment = {
                submitAction.invoke(it)
                coroutineScope.launch {
                    listState.animateScrollToItem(index = 0)
                }
            })
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

    val alpha = if (displayEditLayout) 0.3f else 1f

    if (comment.editable) {
        EditableCommentInputUI(comment, updateAction, cancelAction)
    } else {
        Column(
            modifier = Modifier
                .padding(horizontal = 32.dp, vertical = 8.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(greyBg)
                .alpha(alpha)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .defaultMinSize(minHeight = minButtonSize)
            ) {
                Text(
                    modifier = Modifier
                        .weight(1f)
                        .padding(top = 8.dp, start = 8.dp),
                    text = commentMsg,
                    style = MyHealthTypography.body2
                )

                Box(modifier = Modifier.wrapContentSize(Alignment.TopStart)) {
                    if (displayEditLayout.not() && comment.syncStatus == SyncStatus.UP_TO_DATE) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_comment_options),
                            contentScale = ContentScale.Inside,
                            modifier = Modifier
                                .width(minButtonSize)
                                .height(minButtonSize)
                                .clickable {
                                    expanded = true
                                },
                            contentDescription = stringResource(id = R.string.edit)
                        )
                    } else {
                        Spacer(modifier = Modifier.size(width = 8.dp, height = 0.dp))
                    }
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

            CommentFooter(comment)
        }
    }
}

@Composable
private fun CommentFooter(comment: Comment) {
    val footer = with(comment) {
        if (syncStatus == SyncStatus.UP_TO_DATE && date != null) {
            date.toDateTimeString()
        } else {
            syncStatus.getDescription()?.let { stringResource(id = it) }.orEmpty()
        }
    }

    Text(
        text = footer,
        style = MyHealthTypography.caption.copy(color = grey),
        modifier = Modifier.padding(bottom = 8.dp, start = 8.dp, end = 8.dp)
    )
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
        text = "This is a long comment that should break the line",
        date = date,
        version = 0L,
        syncStatus = SyncStatus.UP_TO_DATE,
        entryTypeCode = "",
        createdBy = "",
        createdDateTime = date,
        updatedDateTime = date,
        updatedBy = ""
    )

    val comments = listOf(
        comment,
        comment.copy(syncStatus = SyncStatus.EDIT),
        comment.copy(syncStatus = SyncStatus.INSERT),
        comment.copy(syncStatus = SyncStatus.DELETE),
    )

    val uiState = CommentsUiState(commentsList = comments)
    CommentsScreen(uiState, {}, {}, {}, {}, {})
}

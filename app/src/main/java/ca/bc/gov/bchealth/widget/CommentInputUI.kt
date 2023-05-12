package ca.bc.gov.bchealth.widget

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedButton
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import ca.bc.gov.bchealth.R
import ca.bc.gov.bchealth.compose.BasePreview
import ca.bc.gov.bchealth.compose.MyHealthTheme
import ca.bc.gov.bchealth.compose.MyHealthTypography
import ca.bc.gov.bchealth.compose.blue
import ca.bc.gov.bchealth.compose.darkText
import ca.bc.gov.bchealth.compose.grey
import ca.bc.gov.bchealth.compose.greyBg
import ca.bc.gov.bchealth.compose.minButtonSize
import ca.bc.gov.bchealth.compose.primaryBlue
import ca.bc.gov.bchealth.compose.red
import ca.bc.gov.bchealth.compose.white
import ca.bc.gov.bchealth.ui.comment.Comment
import ca.bc.gov.common.model.SyncStatus
import java.time.Instant

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun CommentInputUI(onSubmitComment: (String) -> Unit) {
    var comment by rememberSaveable { mutableStateOf("") }
    var validation by rememberSaveable { mutableStateOf(CommentValidation.VALID) }
    val keyboardController = LocalSoftwareKeyboardController.current

    val submitAction: (String) -> Unit = {
        if (validation == CommentValidation.VALID && onSubmitComment != {}) {
            onSubmitComment.invoke(it)
            comment = ""
            keyboardController?.hide()
        }
    }

    Column(
        modifier = Modifier
            .background(white)
            .padding(bottom = 12.dp)
    ) {

        ShadowSpacer()

        OutlinedTextField(
            value = comment,
            onValueChange = {
                comment = it
                validation = validateComment(comment)
            },
            label = {
                Text(
                    text = stringResource(id = R.string.comment_here),
                    color = if (validation == CommentValidation.VALID) grey else red
                )
            },
            isError = validation != CommentValidation.VALID,
            modifier = Modifier
                .padding(top = 8.dp, bottom = 8.dp, end = 32.dp, start = 32.dp)
                .fillMaxWidth(),
            singleLine = true,
            leadingIcon = {
                Icon(
                    painter = painterResource(id = R.drawable.ic_lock_solid_grey),
                    contentDescription = null,
                    tint = if (validation == CommentValidation.VALID) grey else red
                )
            },
            trailingIcon = {
                TrailingIcon(
                    comment,
                    validation == CommentValidation.VALID,
                    submitAction
                )
            },
            colors = getOutlineColors(),
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Send,
                keyboardType = KeyboardType.Text
            ),
            keyboardActions = KeyboardActions(
                onSend = {
                    submitAction(comment)
                }
            ),
        )

        if (validation == CommentValidation.EXCEEDS_LENGTH) {
            ErrorMessage(R.string.comments_error_max_character)
        }
    }
}

@Composable
fun EditableCommentInputUI(
    comment: Comment,
    onUpdate: (Comment) -> Unit,
    onCancel: (Comment) -> Unit,
) {
    var content by rememberSaveable { mutableStateOf(comment.text.orEmpty()) }
    var validation by rememberSaveable { mutableStateOf(CommentValidation.VALID) }

    Column(
        modifier = Modifier.padding(vertical = 8.dp),
        horizontalAlignment = Alignment.End
    ) {
        TextField(
            value = content,
            onValueChange = {
                content = it
                validation = validateComment(content)
            },
            isError = validation != CommentValidation.VALID,
            modifier = Modifier
                .padding(top = 8.dp, bottom = 8.dp, end = 32.dp, start = 32.dp)
                .fillMaxWidth(),
            colors = getInputColors(),
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Text),
        )

        if (validation == CommentValidation.EXCEEDS_LENGTH) {
            ErrorMessage(R.string.comments_error_max_character)
        }

        Row(
            Modifier.padding(top = 8.dp, bottom = 8.dp, end = 32.dp)
        ) {
            OutlinedButton(
                onClick = { onCancel.invoke(comment) },
                border = BorderStroke(1.dp, primaryBlue),
                modifier = Modifier.defaultMinSize(minHeight = minButtonSize),
            ) {
                Text(
                    text = stringResource(id = R.string.cancel),
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colors.primary,
                    style = MyHealthTypography.button,
                )
            }

            Button(
                onClick = { onUpdate.invoke(comment.copy(text = content)) },
                enabled = validation == CommentValidation.VALID,
                modifier = Modifier
                    .padding(start = 12.dp)
                    .defaultMinSize(minHeight = minButtonSize),
            ) {
                Text(
                    text = stringResource(id = R.string.comment_update),
                    textAlign = TextAlign.Center,
                    style = MyHealthTypography.button,
                )
            }
        }
    }
}

private fun validateComment(content: String): CommentValidation =
    if (content.isBlank()) {
        CommentValidation.BLANK
    } else if (content.length > 1000) {
        CommentValidation.EXCEEDS_LENGTH
    } else {
        CommentValidation.VALID
    }

@Composable
private fun getOutlineColors() = TextFieldDefaults.outlinedTextFieldColors(
    focusedBorderColor = grey,
    unfocusedBorderColor = grey,
    errorBorderColor = red,
    errorLeadingIconColor = red,
    errorTrailingIconColor = red,
    errorLabelColor = red
)

@Composable
private fun getInputColors() = TextFieldDefaults.outlinedTextFieldColors(
    errorBorderColor = red,
    backgroundColor = greyBg,
    textColor = darkText,
)

@Composable
private fun TrailingIcon(
    comment: String,
    isCommentValid: Boolean,
    onSubmitComment: (String) -> Unit
) {
    val iconId: Int
    val onClickIcon: () -> Unit
    val tint: Color

    if (comment.isBlank()) {
        iconId = R.drawable.ic_add_comment
        onClickIcon = {}
        tint = grey
    } else {
        iconId = R.drawable.ic_add_comment_press
        onClickIcon = {
            onSubmitComment(comment)
        }
        tint = blue
    }

    Icon(
        modifier = Modifier.clickable(onClick = onClickIcon),
        painter = painterResource(id = iconId),
        contentDescription = stringResource(id = R.string.comment_here),
        tint = if (isCommentValid) tint else red
    )
}

@Composable
private fun ErrorMessage(messageId: Int) {
    Text(
        text = stringResource(id = messageId),
        style = MyHealthTypography.overline.copy(color = red),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 32.dp),
    )
}

@Composable
private fun ShadowSpacer() {
    val brush = Brush.verticalGradient(
        listOf(
            Color(0x00000000),
            Color(0x09000000),
            Color(0x26000000),
        )
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(4.dp)
            .background(brush)
    )
}

private enum class CommentValidation {
    VALID,
    EXCEEDS_LENGTH,
    BLANK,
}

@Composable
@BasePreview
private fun PreviewCommentInputUI() {
    CommentInputUI {}
}

@Composable
@BasePreview
private fun PreviewCommentInputEditUI() {
    val date = Instant.now()

    val comment = Comment(
        text = "comment01",
        date = null,
        version = 0L,
        syncStatus = SyncStatus.UP_TO_DATE,
        entryTypeCode = "",
        createdBy = "",
        createdDateTime = date,
        updatedDateTime = date,
        updatedBy = ""
    )

    MyHealthTheme {
        EditableCommentInputUI(comment, {}) {}
    }
}

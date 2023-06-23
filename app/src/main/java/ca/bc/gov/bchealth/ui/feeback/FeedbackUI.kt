package ca.bc.gov.bchealth.ui.feeback

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import ca.bc.gov.bchealth.R
import ca.bc.gov.bchealth.compose.BasePreview
import ca.bc.gov.bchealth.compose.MyHealthTypography
import ca.bc.gov.bchealth.compose.SmallDevicePreview
import ca.bc.gov.bchealth.compose.minButtonSize
import ca.bc.gov.bchealth.compose.theme.black
import ca.bc.gov.bchealth.compose.theme.grey
import ca.bc.gov.bchealth.compose.theme.lightBlue
import ca.bc.gov.bchealth.compose.theme.red
import ca.bc.gov.bchealth.model.validation.BaseTextValidation
import ca.bc.gov.bchealth.model.validation.BaseTextValidation.BLANK
import ca.bc.gov.bchealth.model.validation.BaseTextValidation.EXCEEDS_LENGTH
import ca.bc.gov.bchealth.model.validation.BaseTextValidation.VALID
import ca.bc.gov.bchealth.ui.custom.MyHealthScaffold
import kotlinx.coroutines.flow.StateFlow

private const val MAX_LENGTH = 500

@Composable
fun FeedbackUI(
    uiStateFlow: StateFlow<FeedbackUiState>,
    navigationAction: () -> Unit,
    sendAction: (String) -> Unit,
    onMessageSent: () -> Unit,
    onError: () -> Unit,
) {
    val uiState = uiStateFlow.collectAsState().value

    MyHealthScaffold(
        title = stringResource(id = R.string.feedback_title),
        navigationAction = navigationAction,
        isLoading = uiState.isLoading
    ) {
        FeedbackContent(uiState, sendAction, onMessageSent)
    }

    if (uiState.error != null) onError.invoke()
}

@Composable
private fun FeedbackContent(
    uiState: FeedbackUiState,
    sendAction: (String) -> Unit,
    onMessageSent: () -> Unit,
) {
    var message by rememberSaveable { mutableStateOf("") }
    var validation by rememberSaveable { mutableStateOf(BLANK) }

    if (uiState.requestSucceed == true) {
        message = ""
        validation = BLANK
        onMessageSent.invoke()
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            modifier = Modifier.padding(top = 16.dp),
            text = stringResource(id = R.string.feedback_body),
            style = MyHealthTypography.body2.copy(color = black)
        )

        Column(
            modifier = Modifier
                .padding(top = 20.dp)
                .border(
                    width = 1.dp,
                    color = getOutlineColours(validation),
                    shape = RoundedCornerShape(4.dp)
                )
        ) {

            Box {

                HintLabel(message)

                BasicTextField(
                    modifier = Modifier
                        .padding(top = 12.dp, start = 16.dp, end = 12.dp, bottom = 20.dp)
                        .fillMaxWidth()
                        .defaultMinSize(minHeight = 160.dp),
                    value = message,
                    onValueChange = {
                        message = it
                        validation = validateMessage(message)
                    },
                    textStyle = MyHealthTypography.body2.copy(color = grey),
                    maxLines = 10,
                )
            }
            CharsCount(message, validation)
        }

        ErrorMessage(validation)

        Spacer(modifier = Modifier.weight(1f))

        SendButton(message, validation, sendAction)
    }
}

@Composable
private fun HintLabel(message: String) {
    if (message.isEmpty()) {
        Text(
            style = MyHealthTypography.body2.copy(color = grey),
            modifier = Modifier.padding(top = 12.dp, start = 18.dp, end = 12.dp),
            text = stringResource(id = R.string.feedback_input_hint),
        )
    }
}

@Composable
private fun SendButton(
    message: String,
    validation: BaseTextValidation,
    sendAction: (String) -> Unit
) {
    Button(
        onClick = { sendAction.invoke(message) },
        enabled = validation == VALID,
        modifier = Modifier
            .padding(bottom = 32.dp)
            .defaultMinSize(minHeight = minButtonSize)
            .fillMaxWidth(),
        colors = ButtonDefaults.buttonColors(disabledBackgroundColor = lightBlue)
    ) {
        Text(
            text = stringResource(id = R.string.feedback_button),
            textAlign = TextAlign.Center,
            style = MyHealthTypography.button,
        )
    }
}

@Composable
private fun getOutlineColours(validation: BaseTextValidation): Color =
    if (validation == EXCEEDS_LENGTH) red else grey

@Composable
private fun CharsCount(input: String, validation: BaseTextValidation) {
    val count = "${input.length}/$MAX_LENGTH"
    val colour = if (validation == EXCEEDS_LENGTH) red else grey

    Text(
        text = count,
        style = MyHealthTypography.overline.copy(color = colour),
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, bottom = 12.dp)
    )
}

@Composable
private fun ErrorMessage(validation: BaseTextValidation) {
    if (validation == EXCEEDS_LENGTH) {
        Text(
            text = stringResource(id = R.string.feedback_input_error, MAX_LENGTH),
            style = MyHealthTypography.overline.copy(color = red),
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp)
        )
    }
}

private fun validateMessage(message: String): BaseTextValidation =
    if (message.length > MAX_LENGTH) {
        EXCEEDS_LENGTH
    } else if (message.isBlank()) {
        BLANK
    } else {
        VALID
    }

@SmallDevicePreview
@BasePreview
@Composable
private fun PreviewFeedbackContent() {
    FeedbackContent(FeedbackUiState(), {}, {})
}

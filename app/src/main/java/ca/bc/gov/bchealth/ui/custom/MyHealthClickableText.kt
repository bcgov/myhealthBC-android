package ca.bc.gov.bchealth.ui.custom

import androidx.compose.foundation.text.ClickableText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import ca.bc.gov.bchealth.compose.primaryBlue

private const val TEXT_TAG = "TEXT_TAG"

@Composable
fun MyHealthClickableText(
    modifier: Modifier = Modifier,
    style: TextStyle = TextStyle.Default,
    fullText: String,
    clickableText: String,
    action: () -> Unit
) {
    val nonClickableText = getNonClickableText(fullText, clickableText)

    val annotatedText = buildAnnotatedString {
        if (nonClickableText == null) {
            append(fullText)
        } else {
            append(nonClickableText.first)
            pushStringAnnotation(tag = TEXT_TAG, annotation = "")
            withStyle(
                style = SpanStyle(
                    color = primaryBlue,
                    textDecoration = TextDecoration.Underline
                )
            ) {
                append(clickableText)
            }

            append(nonClickableText.second)
            pop()
        }
    }

    ClickableText(
        modifier = modifier,
        style = style,
        text = annotatedText,
        onClick = {
            annotatedText.getStringAnnotations(tag = TEXT_TAG, start = it, end = it)
                .firstOrNull()?.let {
                    action.invoke()
                }
        }
    )
}

private fun getNonClickableText(
    fullText: String,
    clickableText: String
): Pair<String, String>? {
    val splitResult = fullText.split(
        delimiters = arrayOf(clickableText),
        ignoreCase = false,
        limit = 2
    )

    if (splitResult.size != 2) return null

    return splitResult[0] to splitResult[1]
}

@Composable
@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
private fun PreviewMyHealthClickableText() {
    val fullText = "This is a FAQ page."
    val clickableText = "FAQ"
    MyHealthClickableText(fullText = fullText, clickableText = clickableText) {}
}

package ca.bc.gov.bchealth.compose.component.m3

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

/**
 * @author pinakin.kansara
 * Created 2023-11-29 at 3:21 p.m.
 */
@Composable
fun HGErrorDialog(
    onPositiveBtnClick: () -> Unit,
    onNegativeBtnClick: () -> Unit,
    title: String,
    message: String?,
    positiveBtnLabel: String,
    negativeBtnLabel: String?
) {
    AlertDialog(
        onDismissRequest = onNegativeBtnClick,
        title = { Text(text = title) },
        text = message?.let {
            { Text(text = message) }
        },
        confirmButton = {
            DialogBtn(onClick = onPositiveBtnClick, text = positiveBtnLabel)
        },
        dismissButton = negativeBtnLabel?.let {
            {
                DialogBtn(onClick = onNegativeBtnClick, text = it)
            }
        }
    )
}

@Composable
private fun DialogBtn(onClick: () -> Unit, text: String) {
    HGTextButton(
        onClick = onClick
    ) {
        Text(text = text)
    }
}

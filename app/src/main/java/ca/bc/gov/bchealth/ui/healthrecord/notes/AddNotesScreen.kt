package ca.bc.gov.bchealth.ui.healthrecord.notes

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.toggleable
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Switch
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import ca.bc.gov.bchealth.R
import ca.bc.gov.bchealth.compose.BasePreview
import ca.bc.gov.bchealth.compose.MyHealthTheme
import ca.bc.gov.bchealth.compose.descriptionGrey
import ca.bc.gov.bchealth.compose.disabledTextColor
import ca.bc.gov.bchealth.compose.grey
import ca.bc.gov.bchealth.compose.white

@Composable
fun AddNotesScreen(
    modifier: Modifier
) {
    AddNotesScreenContent(modifier)
}

@Composable
private fun AddNotesScreenContent(
    modifier: Modifier = Modifier
) {

    Column(
        modifier = modifier
            .padding(32.dp)
            .fillMaxSize()
    ) {
        var title by rememberSaveable(stateSaver = TextFieldValue.Saver) {
            mutableStateOf(TextFieldValue("", TextRange(0, 7)))
        }
        TextField(
            modifier = Modifier.fillMaxWidth(),
            value = title,
            placeholder = {
                Text(
                    text = stringResource(id = R.string.untitled),
                    style = MaterialTheme.typography.h2,
                    color = disabledTextColor
                )
            },
            onValueChange = { title = it },
            textStyle = MaterialTheme.typography.h2,
            colors = TextFieldDefaults.textFieldColors(
                backgroundColor = white,
                focusedIndicatorColor = white,
                disabledIndicatorColor = white,
                errorIndicatorColor = white,
                unfocusedIndicatorColor = white
            )
        )

        Spacer(modifier = Modifier.height(16.dp))
        Row(modifier = Modifier.fillMaxWidth()) {
        }
        Spacer(modifier = Modifier.height(16.dp))

        Divider(modifier = Modifier.fillMaxWidth(), color = grey, thickness = 1.dp)
        var pineappleOnPizza by remember { mutableStateOf(true) }
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            Modifier
                .toggleable(
                    role = Role.Switch,
                    value = pineappleOnPizza,
                    onValueChange = { pineappleOnPizza = it },
                )
        ) {
            Text(
                modifier = Modifier.weight(1f, true),
                text = stringResource(id = R.string.add_to_my_timeline)
            )
            Switch(checked = pineappleOnPizza, onCheckedChange = null)
        }
        Text(modifier = Modifier.fillMaxWidth(), text = "May 25, 2022")
        Spacer(modifier = Modifier.height(16.dp))
        Divider(modifier = Modifier.fillMaxWidth(), color = grey, thickness = 1.dp)

        var description by rememberSaveable(stateSaver = TextFieldValue.Saver) {
            mutableStateOf(TextFieldValue("", TextRange(0, 7)))
        }
        TextField(
            modifier = Modifier.fillMaxSize(),
            value = description,
            placeholder = {
                Text(
                    text = stringResource(id = R.string.tap_to_start_typing),
                    style = MaterialTheme.typography.body2,
                    color = descriptionGrey
                )
            },
            onValueChange = { description = it },
            textStyle = MaterialTheme.typography.body2,
            colors = TextFieldDefaults.textFieldColors(
                backgroundColor = white,
                focusedIndicatorColor = white,
                disabledIndicatorColor = white,
                errorIndicatorColor = white,
                unfocusedIndicatorColor = white
            )
        )
    }
}

@BasePreview
@Composable
private fun AddNotesScreenPreview() {
    MyHealthTheme {
        AddNotesScreenContent()
    }
}

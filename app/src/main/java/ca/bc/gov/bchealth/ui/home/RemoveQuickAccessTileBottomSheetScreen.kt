package ca.bc.gov.bchealth.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import ca.bc.gov.bchealth.R
import ca.bc.gov.bchealth.compose.BasePreview
import ca.bc.gov.bchealth.compose.component.HGButton
import ca.bc.gov.bchealth.compose.component.HGButtonDefaults
import ca.bc.gov.bchealth.compose.component.HGTextButton
import ca.bc.gov.bchealth.compose.theme.HealthGatewayTheme
import ca.bc.gov.bchealth.compose.theme.blue
import ca.bc.gov.bchealth.compose.theme.descriptionGrey
import ca.bc.gov.bchealth.compose.theme.dividerGrey

@Composable
fun RemoveQuickAccessTileBottomSheetScreen(
    modifier: Modifier = Modifier,
    viewModel: RemoveQuickAccessTileViewModel,
    id: Long = 0,
    name: String,
    onRemoveClicked: () -> Unit,
    ondDismissClicked: () -> Unit
) {
    RemoveQuickAccessTileBottomSheetContent(
        modifier = modifier,
        name = name,
        onRemoveClicked = {
            viewModel.updateTile(id)
            onRemoveClicked()
        },
        ondDismissClicked
    )
}

@Composable
private fun RemoveQuickAccessTileBottomSheetContent(
    modifier: Modifier = Modifier,
    name: String,
    onRemoveClicked: () -> Unit,
    ondDismissClicked: () -> Unit
) {

    Column(
        modifier = modifier
            .padding(16.dp)
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Text(
            text = stringResource(id = R.string.remove_tile_confirmation_title, name),
            style = MaterialTheme.typography.subtitle2,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = stringResource(id = R.string.remove_tile_confirmation_description),
            style = MaterialTheme.typography.body2,
            color = descriptionGrey
        )
        Spacer(modifier = Modifier.height(16.dp))
        Divider(
            modifier = Modifier.fillMaxWidth(),
            color = dividerGrey,
            thickness = 1.dp
        )
        Spacer(modifier = Modifier.height(16.dp))
        HGButton(
            modifier = Modifier.fillMaxWidth(),
            onClick = onRemoveClicked,
            text = stringResource(id = R.string.remove),
            defaultHeight = HGButtonDefaults.SmallButtonHeight
        )
        Spacer(modifier = Modifier.height(16.dp))
        HGTextButton(onClick = ondDismissClicked) {
            Text(
                text = stringResource(id = R.string.dismiss),
                style = MaterialTheme.typography.body2,
                fontWeight = FontWeight.Bold,
                color = blue,
                textDecoration = TextDecoration.Underline
            )
        }
    }
}

@Composable
@BasePreview
private fun RemoveQuickAccessTileBottomSheetScreenPreview() {
    HealthGatewayTheme {
        RemoveQuickAccessTileBottomSheetContent(
            name = "Test",
            onRemoveClicked = {},
            ondDismissClicked = {}
        )
    }
}

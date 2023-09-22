package ca.bc.gov.bchealth.compose.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material.ChipDefaults
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.FilterChip
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import ca.bc.gov.bchealth.compose.BasePreview
import ca.bc.gov.bchealth.compose.theme.HealthGatewayTheme
import ca.bc.gov.bchealth.compose.theme.white

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun HGFilterChip(
    text: String,
    selected: Boolean = false,
    onClick: () -> Unit
) {

    FilterChip(
        selected = selected,
        onClick = { onClick() },
        shape = MaterialTheme.shapes.small.copy(CornerSize(8.dp)),
        border = BorderStroke(
            1.dp, MaterialTheme.colors.primary
        ),
        colors = ChipDefaults.outlinedFilterChipColors(
            selectedContentColor = MaterialTheme.colors.background,
            selectedBackgroundColor = MaterialTheme.colors.primary
        )
    ) {
        Text(
            text,
            color = if (selected) {
                white
            } else {
                MaterialTheme.colors.primary
            },
            style = MaterialTheme.typography.body1
        )
    }
}

@Composable
@BasePreview
private fun HGChipPreview() {
    HealthGatewayTheme {
        HGFilterChip("Hello world", selected = false, onClick = {})
    }
}

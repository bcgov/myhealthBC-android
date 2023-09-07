package ca.bc.gov.bchealth.compose.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import ca.bc.gov.bchealth.R
import ca.bc.gov.bchealth.compose.BasePreview
import ca.bc.gov.bchealth.compose.theme.HealthGatewayTheme

@Composable
fun HorizontalFilterGroupUI(
    onFilterCleared: () -> Unit = {},
    modifier: Modifier = Modifier,
    filtersApplied: List<String>
) {
    Row(modifier = modifier) {
        IconButton(onClick = onFilterCleared) {
            Icon(
                painterResource(id = R.drawable.ic_clear),
                contentDescription = stringResource(id = R.string.clear_all),
                tint = MaterialTheme.colors.primary
            )
        }
        LazyRow(
            modifier = Modifier.fillMaxWidth(1f),
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(filtersApplied) {
                HGFilterChip(text = it) {
                }
            }
        }
    }
}

@Composable
@BasePreview
private fun AppliedFilterUIPreview() {
    HealthGatewayTheme {
        HorizontalFilterGroupUI(modifier = Modifier.fillMaxWidth(), filtersApplied = emptyList())
    }
}

package ca.bc.gov.bchealth.compose.component

import android.widget.SearchView
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import ca.bc.gov.bchealth.R
import ca.bc.gov.bchealth.compose.BasePreview
import ca.bc.gov.bchealth.compose.theme.HealthGatewayTheme
import ca.bc.gov.bchealth.compose.theme.grey

@Composable
fun SearchBarUI(
    onFilterClicked: () -> Unit = {},
    onSearchQuery: (String) -> Unit,
    modifier: Modifier = Modifier,
    enableFilter: Boolean = true
) {
    Row(
        modifier = modifier
            .border(
                width = 1.dp,
                shape = MaterialTheme.shapes.medium,
                color = grey
            )
    ) {
        AndroidView(
            modifier = Modifier.weight(1f, fill = true),
            factory = {
                SearchView(it).apply {
                    isIconifiedByDefault = false
                    queryHint = it.getString(R.string.search_hint)
                }
            },
            update = {
                it.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                    override fun onQueryTextSubmit(query: String?): Boolean {
                        // NO Implementation
                        return false
                    }

                    override fun onQueryTextChange(newText: String?): Boolean {
                        newText?.let { query ->
                            onSearchQuery(query)
                        }
                        return true
                    }
                })
            }
        )

        if (enableFilter) {
            Image(
                modifier = Modifier
                    .clickable { onFilterClicked() }
                    .size(width = 48.dp, height = 48.dp)
                    .padding(12.dp),
                painter = painterResource(id = R.drawable.ic_filter),
                contentDescription = "Filter"
            )
        }
    }
}

@Composable
@BasePreview
private fun SearchBarUiPreview() {
    HealthGatewayTheme {
        SearchBarUI(onSearchQuery = {})
    }
}

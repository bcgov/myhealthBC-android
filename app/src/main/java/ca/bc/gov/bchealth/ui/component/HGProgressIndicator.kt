package ca.bc.gov.bchealth.ui.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import ca.bc.gov.bchealth.compose.BasePreview
import ca.bc.gov.bchealth.compose.MyHealthTheme

@Composable
fun HGProgressIndicator(modifier: Modifier) {

    Box(modifier = modifier.fillMaxSize()) {
        CircularProgressIndicator(
            modifier = Modifier.align(Alignment.Center),
        )
    }
}

@BasePreview
@Composable
private fun HGProgressIndicatorPreview() {
    MyHealthTheme {
        HGProgressIndicator(modifier = Modifier)
    }
}

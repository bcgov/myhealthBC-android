package ca.bc.gov.bchealth.compose.component

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import ca.bc.gov.bchealth.R
import ca.bc.gov.bchealth.compose.BasePreview
import ca.bc.gov.bchealth.compose.largeButton
import ca.bc.gov.bchealth.compose.theme.HealthGatewayTheme
import ca.bc.gov.bchealth.compose.theme.grey

@Composable
fun EmptyStateUI(
    modifier: Modifier = Modifier,
    @DrawableRes image: Int,
    @StringRes title: Int,
    @StringRes description: Int
) {
    Column(
        modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Image(
            painter = painterResource(id = image),
            contentDescription = null
        )

        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = stringResource(id = title),
            style = MaterialTheme.typography.subtitle1,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colors.primary
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = stringResource(id = description),
            style = MaterialTheme.typography.largeButton,
            fontWeight = FontWeight.Normal,
            color = grey,
            textAlign = TextAlign.Center
        )
    }
}

@BasePreview
@Composable
private fun HGEmptyStatePreview() {
    HealthGatewayTheme {
        EmptyStateUI(
            image = R.drawable.ic_no_record,
            title = R.string.no_records_found,
            description = R.string.refresh
        )
    }
}

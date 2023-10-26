package ca.bc.gov.bchealth.compose.component

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.res.painterResource
import ca.bc.gov.bchealth.R
import ca.bc.gov.bchealth.compose.BasePreview
import ca.bc.gov.bchealth.compose.theme.HealthGatewayTheme

@Composable
fun HGProgressIndicator(modifier: Modifier = Modifier) {

    Box(modifier = modifier.fillMaxSize()) {
        val infiniteTransition = rememberInfiniteTransition()
        val heartbeatAnimation by infiniteTransition.animateFloat(
            initialValue = 1f,
            targetValue = 1.4f,
            animationSpec = infiniteRepeatable(
                animation = tween(1000),
                repeatMode = RepeatMode.Reverse
            )
        )

        Icon(
            modifier = Modifier.align(Alignment.Center)
                .scale(heartbeatAnimation),
            painter = painterResource(id = R.drawable.ic_heart),
            contentDescription = null,
            tint = MaterialTheme.colors.primary
        )
    }
}

@Composable
@BasePreview
private fun HGProgressIndicatorPreview() {
    HealthGatewayTheme {
        HGProgressIndicator()
    }
}

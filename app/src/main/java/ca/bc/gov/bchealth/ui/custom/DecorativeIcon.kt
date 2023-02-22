package ca.bc.gov.bchealth.ui.custom

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import ca.bc.gov.bchealth.R

@Composable
fun DecorativeImage(@DrawableRes resourceId: Int, modifier: Modifier = Modifier) {
    Image(
        modifier = modifier,
        painter = painterResource(id = resourceId),
        contentDescription = null,
    )
}

@Preview
@Composable
fun PreviewDecorativeIcon() {
    DecorativeImage(
        resourceId = R.drawable.ic_profile_image,
    )
}

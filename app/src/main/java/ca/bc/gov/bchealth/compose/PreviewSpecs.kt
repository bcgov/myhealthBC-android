package ca.bc.gov.bchealth.compose

import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview

@Preview(
    name = "Pixel5",
    showBackground = true, backgroundColor = 0xFFFFFFFF,
    device = "id:pixel_5",
)
annotation class BasePreview

@Preview(
    name = "Nexus One",
    showBackground = true, backgroundColor = 0xFFFFFFFF,
    device = "id:3.7in WVGA (Nexus One)",
)
annotation class SmallDevicePreview

@Preview(
    showBackground = true, backgroundColor = 0xFFFFFFFF,
    widthDp = 840,
    device = "id:pixel_xl"
)
annotation class LandscapeTabletPreview

@Preview(
    name = "Pixel5", device = "id:pixel_5", showBackground = true, showSystemUi = true
)
@Preview(
    name = "PixelC", device = "id:pixel_c", showBackground = true, showSystemUi = true,
)
@Preview(
    name = "Foldable", device = Devices.FOLDABLE, showBackground = true, showSystemUi = true,
)
annotation class MultiDevicePreview

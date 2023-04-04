package ca.bc.gov.bchealth.compose

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

package ca.bc.gov.bchealth.compose.theme

import androidx.compose.material.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import ca.bc.gov.bchealth.R

val hgFonts = FontFamily(
    Font(R.font.bc_sans_regular, weight = FontWeight.Normal, style = FontStyle.Normal),
    Font(R.font.bc_sans_bold, weight = FontWeight.Bold, style = FontStyle.Normal),
    Font(R.font.bc_sans_italic, weight = FontWeight.Normal, style = FontStyle.Italic),
    Font(R.font.bc_sans_bold_italic, weight = FontWeight.Bold, style = FontStyle.Italic),
)

internal val HealthGatewayTypography = Typography(
    defaultFontFamily = hgFonts,
    h1 = TextStyle(
        fontSize = 60.sp,
        lineHeight = 72.sp,
        letterSpacing = 0.sp,
    ),
    h2 = TextStyle(
        fontSize = 50.sp,
        lineHeight = 64.sp,
        letterSpacing = 0.sp,
    ),
    h3 = TextStyle(
        fontSize = 40.sp,
        lineHeight = 56.sp,
        letterSpacing = 0.sp,
    ),
    h4 = TextStyle(
        fontSize = 33.sp,
        lineHeight = 40.sp,
        letterSpacing = 0.sp,
    ),
    h5 = TextStyle(
        fontSize = 24.sp,
        lineHeight = 32.sp,
        letterSpacing = 0.sp,
    ),
    subtitle1 = TextStyle(
        fontSize = 20.sp,
        lineHeight = 38.sp,
        letterSpacing = 0.sp,
    ),
    subtitle2 = TextStyle(
        fontSize = 17.sp,
        lineHeight = 34.sp,
        letterSpacing = 0.sp,
    ),
    body1 = TextStyle(
        fontSize = 15.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.sp,
    ),
    body2 = TextStyle(
        fontSize = 13.sp,
        lineHeight = 22.sp,
        fontStyle = FontStyle.Normal,
        letterSpacing = 0.sp,
    )
)

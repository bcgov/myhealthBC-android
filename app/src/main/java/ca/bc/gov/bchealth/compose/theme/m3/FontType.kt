package ca.bc.gov.bchealth.compose.theme.m3

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import ca.bc.gov.bchealth.R

/**
 * @author pinakin.kansara
 * Created 2023-10-16 at 12:15 p.m.
 */
val hgFonts = FontFamily(
    Font(R.font.bc_sans_regular, weight = FontWeight.Normal, style = FontStyle.Normal),
    Font(R.font.bc_sans_bold, weight = FontWeight.Bold, style = FontStyle.Normal),
    Font(R.font.bc_sans_italic, weight = FontWeight.Normal, style = FontStyle.Italic),
    Font(R.font.bc_sans_bold_italic, weight = FontWeight.Bold, style = FontStyle.Italic),
)

internal val HealthGatewayTypography = Typography(
    displayLarge = TextStyle(
        fontSize = 60.sp,
        letterSpacing = 0.sp,
        fontFamily = hgFonts
    ),
    displayMedium = TextStyle(
        fontSize = 50.sp,
        letterSpacing = 0.sp,
        fontFamily = hgFonts
    ),
    displaySmall = TextStyle(
        fontSize = 40.sp,
        letterSpacing = 0.sp,
        fontFamily = hgFonts
    ),
    headlineLarge = TextStyle(
        fontSize = 33.sp,
        letterSpacing = 0.sp,
        fontFamily = hgFonts
    ),
    headlineSmall = TextStyle(
        fontSize = 24.sp,
        letterSpacing = 0.sp,
        fontFamily = hgFonts
    ),
    titleLarge = TextStyle(
        fontSize = 20.sp,
        letterSpacing = 0.sp,
        fontFamily = hgFonts
    ),
    titleMedium = TextStyle(
        fontSize = 17.sp,
        letterSpacing = 0.sp,
        fontFamily = hgFonts
    ),
    titleSmall = TextStyle(
        fontSize = 15.sp,
        letterSpacing = 0.sp,
        fontFamily = hgFonts
    ),
    bodyMedium = TextStyle(
        fontSize = 13.sp,
        letterSpacing = 0.sp,
        fontFamily = hgFonts
    )
)

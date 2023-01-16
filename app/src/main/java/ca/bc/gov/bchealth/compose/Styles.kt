package ca.bc.gov.bchealth.compose

import androidx.compose.material.Typography
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import ca.bc.gov.bchealth.R

val fonts = FontFamily(
    Font(R.font.bc_sans_regular, weight = FontWeight.Normal, style = FontStyle.Normal),
    Font(R.font.bc_sans_bold, weight = FontWeight.Bold, style = FontStyle.Normal),
    Font(R.font.bc_sans_italic, weight = FontWeight.Normal, style = FontStyle.Italic),
    Font(R.font.bc_sans_bold_italic, weight = FontWeight.Bold, style = FontStyle.Italic),
)

private val darkText = Color(0xFF313132)

val statusBlue30 = Color(0x4D38598A)

val MyHealthTypography = Typography(
    body1 = TextStyle(
        fontFamily = fonts,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        color = darkText
    ),

    body2 = TextStyle(
        fontFamily = fonts,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        color = darkText
    ),
)

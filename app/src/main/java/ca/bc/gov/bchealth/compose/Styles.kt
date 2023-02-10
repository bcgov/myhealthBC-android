package ca.bc.gov.bchealth.compose

import androidx.compose.material.MaterialTheme
import androidx.compose.material.Typography
import androidx.compose.runtime.Composable
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

val white = Color(0xFFFFFFFF)
val primaryBlue = Color(0xFF003366)
val statusBlue30 = Color(0x4D38598A)
val descriptionGrey = Color(0xFF6D757D)

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

    button = TextStyle(
        fontFamily = fonts,
        fontWeight = FontWeight.Bold,
        fontSize = 16.sp,
        color = primaryBlue
    ),

    h3 = TextStyle(
        fontFamily = fonts,
        fontWeight = FontWeight.Bold,
        fontSize = 17.sp,
        color = darkText
    ),
)

fun TextStyle.bold() = this.copy(fontWeight = FontWeight.Bold)

fun TextStyle.italic() = this.copy(fontStyle = FontStyle.Italic)

@Composable
fun MyHealthTheme(content: @Composable () -> Unit) = MaterialTheme(
    colors = MaterialTheme.colors.copy(
        primary = primaryBlue
    ),
    typography = MyHealthTypography,
    shapes = MaterialTheme.shapes,
    content = content
)

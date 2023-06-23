package ca.bc.gov.bchealth.compose

import androidx.compose.material.MaterialTheme
import androidx.compose.material.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ca.bc.gov.bchealth.R
import ca.bc.gov.bchealth.compose.theme.darkText
import ca.bc.gov.bchealth.compose.theme.descriptionGrey
import ca.bc.gov.bchealth.compose.theme.primaryBlue
import ca.bc.gov.bchealth.compose.theme.white

// Consider making touch targets at least 48x48dp. https://support.google.com/accessibility/android/answer/7101858?hl=en
val minButtonSize = 48.dp

val fonts = FontFamily(
    Font(R.font.bc_sans_regular, weight = FontWeight.Normal, style = FontStyle.Normal),
    Font(R.font.bc_sans_bold, weight = FontWeight.Bold, style = FontStyle.Normal),
    Font(R.font.bc_sans_italic, weight = FontWeight.Normal, style = FontStyle.Italic),
    Font(R.font.bc_sans_bold_italic, weight = FontWeight.Bold, style = FontStyle.Italic),
)

val Typography.largeButton: TextStyle
    @Composable
    get() {
        return TextStyle(
            fontFamily = fonts,
            fontStyle = FontStyle.Normal,
            fontSize = 17.sp,
            fontWeight = FontWeight.Bold
        )
    }

val Typography.smallButton: TextStyle
    @Composable
    get() {
        return TextStyle(
            fontFamily = fonts,
            fontStyle = FontStyle.Normal,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
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
        fontStyle = FontStyle.Normal,
        fontSize = 16.sp,
        color = white
    ),

    h2 = TextStyle(
        fontFamily = fonts,
        fontWeight = FontWeight.Bold,
        fontSize = 24.sp,
        color = darkText
    ),

    h3 = TextStyle(
        fontFamily = fonts,
        fontWeight = FontWeight.Bold,
        fontSize = 17.sp,
        color = darkText
    ),

    h4 = TextStyle(
        fontFamily = fonts,
        fontSize = 16.sp,
        color = darkText,
        fontStyle = FontStyle.Normal
    ),

    h6 = TextStyle(
        fontFamily = fonts,
        fontSize = 14.sp,
        color = primaryBlue,
        fontStyle = FontStyle.Normal
    ),

    caption = TextStyle(
        fontFamily = fonts,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        color = descriptionGrey
    ),

    overline = TextStyle(
        fontFamily = fonts,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        color = descriptionGrey
    ),
)

fun TextStyle.bold() = this.copy(fontWeight = FontWeight.Bold)

fun TextStyle.italic() = this.copy(fontStyle = FontStyle.Italic)

@Composable
fun MyHealthTheme(content: @Composable () -> Unit) = MaterialTheme(
    colors = MaterialTheme.colors.copy(
        primary = primaryBlue,
        primaryVariant = white,
        background = white,
        surface = white,
        secondary = primaryBlue,
        secondaryVariant = primaryBlue,
        onBackground = primaryBlue
    ),
    typography = MyHealthTypography,
    shapes = MaterialTheme.shapes,
    content = content
)

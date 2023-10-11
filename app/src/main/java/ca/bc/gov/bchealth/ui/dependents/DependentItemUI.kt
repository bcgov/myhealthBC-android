package ca.bc.gov.bchealth.ui.dependents

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.ConstraintSet
import androidx.constraintlayout.compose.Dimension
import ca.bc.gov.bchealth.R
import ca.bc.gov.bchealth.compose.BasePreview
import ca.bc.gov.bchealth.compose.theme.HealthGatewayTheme
import ca.bc.gov.bchealth.compose.theme.lightGrey

/**
 * @author pinakin.kansara
 * Created 2023-10-23 at 3:19 p.m.
 */
private const val IMG_LEADING_ICON = "img_leading_icon"
private const val TXT_TITLE = "txt_title"
private const val IMG_TRAILING_ICON = "img_trailing_icon"

@Composable
fun DependentItemUI(modifier: Modifier = Modifier, title: String, canUnlink: Boolean = false) {
    Row(
        modifier
            .defaultMinSize(minHeight = 55.dp)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Card(
            modifier = Modifier
                .defaultMinSize(minHeight = 55.dp)
                .weight(1f, fill = true),
            shape = MaterialTheme.shapes.medium,
            backgroundColor = lightGrey,
            elevation = 0.dp,
        ) {

            BoxWithConstraints {
                ConstraintLayout(
                    modifier = Modifier
                        .defaultMinSize(minHeight = 55.dp)
                        .fillMaxWidth(),
                    constraintSet = constraint()
                ) {
                    Image(
                        modifier = Modifier.layoutId(IMG_LEADING_ICON),
                        painter = painterResource(id = R.drawable.ic_manage_dependent),
                        contentDescription = ""
                    )
                    Text(
                        modifier = Modifier.layoutId(TXT_TITLE),
                        text = title,
                        style = MaterialTheme.typography.body1,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colors.primary
                    )
                    Image(
                        modifier = Modifier.layoutId(IMG_TRAILING_ICON),
                        painter = painterResource(id = R.drawable.ic_angle_right),
                        contentDescription = ""
                    )
                }
            }
        }
        AnimatedVisibility(visible = canUnlink) {
            Image(painter = painterResource(id = R.drawable.ic_un_link), contentDescription = "")
        }
    }
}

private fun constraint(): ConstraintSet {
    return ConstraintSet {
        val imgLeadingIcon = createRefFor(IMG_LEADING_ICON)
        val txtTitle = createRefFor(TXT_TITLE)
        val imgTrailingIcon = createRefFor(IMG_TRAILING_ICON)

        constrain(imgLeadingIcon) {
            start.linkTo(parent.start, 16.dp)
            top.linkTo(parent.top)
            bottom.linkTo(parent.bottom)
        }

        constrain(txtTitle) {
            start.linkTo(imgLeadingIcon.end, 16.dp)
            top.linkTo(imgLeadingIcon.top)
            end.linkTo(imgTrailingIcon.start)
            bottom.linkTo(imgLeadingIcon.bottom)
            width = Dimension.fillToConstraints
        }

        constrain(imgTrailingIcon) {
            start.linkTo(txtTitle.end)
            top.linkTo(parent.top)
            end.linkTo(parent.end)
            bottom.linkTo(parent.bottom)
        }
    }
}

@BasePreview
@Composable
private fun DependentItemUIPreview() {

    HealthGatewayTheme {
        DependentItemUI(title = "Hello World")
    }
}

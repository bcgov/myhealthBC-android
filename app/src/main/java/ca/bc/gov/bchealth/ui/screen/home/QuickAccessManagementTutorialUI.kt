package ca.bc.gov.bchealth.ui.screen.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.ConstraintSet
import ca.bc.gov.bchealth.R
import ca.bc.gov.bchealth.compose.MultiDevicePreview
import ca.bc.gov.bchealth.compose.component.HGTextButton
import ca.bc.gov.bchealth.compose.theme.HealthGatewayTheme
import ca.bc.gov.bchealth.compose.theme.statusBlue
import ca.bc.gov.bchealth.compose.theme.white

/**
 * @author pinakin.kansara
 * Created 2024-01-15 at 10:26 a.m.
 */
private const val anchorIconId = "anchorIconId"
private const val bannerBodyId = "bannerBodyId"
@Composable
fun QuickAccessManagementTutorialUI(onDismissTutorialClicked: () -> Unit) {
    Popup(
        alignment = Alignment.TopEnd,
        offset = IntOffset(0, 100)
    ) {
        BoxWithConstraints(modifier = Modifier.wrapContentSize()) {

            ConstraintLayout(constraintSet = quickAccessManagementTutorialConstraint()) {
                Image(
                    modifier = Modifier.layoutId(anchorIconId),
                    painter = painterResource(id = R.drawable.ic_anchor),
                    contentDescription = null
                )
                Column(
                    modifier = Modifier
                        .layoutId(bannerBodyId)
                        .background(statusBlue)
                        .padding(start = 8.dp, top = 8.dp, end = 8.dp)
                ) {
                    Text(
                        modifier = Modifier.padding(start = 8.dp, end = 8.dp),
                        text = stringResource(id = R.string.manage_hint),
                        style = MaterialTheme.typography.body2,
                        color = white
                    )
                    HGTextButton(onClick = { onDismissTutorialClicked() }) {
                        Text(
                            text = "Got it",
                            style = MaterialTheme.typography.body2,
                            fontWeight = FontWeight.Bold,
                            color = white,
                            textDecoration = TextDecoration.Underline
                        )
                    }
                }
            }
        }
    }
}

private fun quickAccessManagementTutorialConstraint(): ConstraintSet {
    return ConstraintSet {
        val anchorIcon = createRefFor(anchorIconId)
        val body = createRefFor(bannerBodyId)

        constrain(anchorIcon) {
            top.linkTo(parent.top, 16.dp)
            end.linkTo(parent.end, 16.dp)
        }

        constrain(body) {
            start.linkTo(parent.start)
            top.linkTo(anchorIcon.bottom)
            end.linkTo(parent.end)
            bottom.linkTo(parent.bottom)
        }
    }
}

@MultiDevicePreview
@Composable
private fun QuickAccessManagementTutorialUIPreview() {
    HealthGatewayTheme {
        QuickAccessManagementTutorialUI(onDismissTutorialClicked = {})
    }
}

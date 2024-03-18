package ca.bc.gov.bchealth.compose.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.ConstraintSet
import androidx.constraintlayout.compose.Dimension
import ca.bc.gov.bchealth.R
import ca.bc.gov.bchealth.compose.BasePreview
import ca.bc.gov.bchealth.compose.theme.HealthGatewayTheme
import ca.bc.gov.bchealth.compose.theme.bannerBackgroundBlue
import ca.bc.gov.bchealth.ui.custom.MyHealthClickableText

private const val BANNER_ICON_ID = "banner_icon_id"
private const val BANNER_BODY_1_ID = "banner_body_1_id"
private const val BANNER_BODY_2_ID = "banner_body_2_id"

@Composable
fun RecordFilterBannerUi(
    onLinkClick: (String) -> Unit,
    modifier: Modifier = Modifier,
    body1: String,
    clickableText: String,
    body2: String
) {

    Card(
        modifier,
        backgroundColor = bannerBackgroundBlue
    ) {
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(16.dp)
        ) {
            val constraint = immunizationBannerConstraints()
            ConstraintLayout(constraint, modifier = Modifier.fillMaxWidth()) {

                Image(
                    modifier = Modifier.layoutId(BANNER_ICON_ID),
                    painter = painterResource(id = R.drawable.ic_info),
                    contentDescription = null
                )

                Text(
                    modifier = Modifier.layoutId(BANNER_BODY_1_ID),
                    text = body1,
                    style = MaterialTheme.typography.body1,
                    color = MaterialTheme.colors.primary
                )

                MyHealthClickableText(
                    modifier = Modifier.layoutId(BANNER_BODY_2_ID),
                    style = MaterialTheme.typography.body1,
                    fullText = body2,
                    clickableText = clickableText,
                    action = { onLinkClick(clickableText) }
                )
            }
        }
    }
}

private fun immunizationBannerConstraints(): ConstraintSet {
    return ConstraintSet {
        val bannerIconId = createRefFor(BANNER_ICON_ID)
        val bannerBody1Id = createRefFor(BANNER_BODY_1_ID)
        val bannerBody2Id = createRefFor(BANNER_BODY_2_ID)

        constrain(bannerIconId) {
            start.linkTo(parent.start)
            top.linkTo(bannerBody1Id.top, 4.dp)
        }

        constrain(bannerBody1Id) {
            start.linkTo(bannerIconId.end, 16.dp)
            top.linkTo(parent.top)
            end.linkTo(parent.end, 16.dp)
            width = Dimension.fillToConstraints
        }

        constrain(bannerBody2Id) {
            start.linkTo(bannerBody1Id.start)
            top.linkTo(bannerBody1Id.bottom)
            end.linkTo(bannerBody1Id.end)
            bottom.linkTo(parent.bottom)
            width = Dimension.fillToConstraints
        }
    }
}

@BasePreview
@Composable
private fun ImmunizationBannerUIPreview() {
    HealthGatewayTheme {
        RecordFilterBannerUi(
            onLinkClick = {},
            body1 = stringResource(id = R.string.records_immunization_banner_top),
            clickableText = stringResource(id = R.string.records_immunization_banner_click),
            body2 = stringResource(id = R.string.records_immunization_banner_click),
        )
    }
}

package ca.bc.gov.bchealth.compose.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.ConstraintSet
import androidx.constraintlayout.compose.Dimension
import androidx.constraintlayout.compose.Visibility
import ca.bc.gov.bchealth.R
import ca.bc.gov.bchealth.compose.BasePreview
import ca.bc.gov.bchealth.compose.theme.HealthGatewayTheme
import ca.bc.gov.bchealth.compose.theme.bannerBackgroundBlue
import ca.bc.gov.bchealth.compose.theme.blue

private const val bannerIconId = "bannerIconId"
private const val bannerTitleId = "bannerTitleId"
private const val bannerArrowId = "bannerArrowId"
private const val bannerBodyId = "bannerBodyId"
private const val buttonLearnModeId = "buttonLearnMoreId"
private const val buttonDismissId = "buttonDismissId"

@Composable
fun AnnouncementBannerUI(
    modifier: Modifier = Modifier,
    title: String,
    description: String,
    onLearnMoreClick: () -> Unit,
    onDismissClick: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    Card(
        modifier.clickable { expanded = !expanded },
        backgroundColor = bannerBackgroundBlue
    ) {
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(16.dp)
        ) {
            val constraint = bannerConstraints(expanded)
            ConstraintLayout(constraint, modifier = Modifier.fillMaxWidth()) {
                Image(
                    modifier = Modifier.layoutId(bannerIconId),
                    painter = painterResource(id = R.drawable.ic_banner_icon),
                    contentDescription = null
                )

                Text(
                    modifier = Modifier.layoutId(bannerTitleId),
                    text = title,
                    style = MaterialTheme.typography.body1,
                    fontWeight = FontWeight.Bold,
                    color = blue
                )

                Image(
                    modifier = Modifier.layoutId(bannerArrowId),
                    painter = painterResource(id = R.drawable.ic_arrow_down),
                    contentDescription = null
                )

                Text(
                    modifier = Modifier.layoutId(bannerBodyId),
                    text = description,
                    style = MaterialTheme.typography.body1
                )

                HGTextButton(
                    onClick = { onDismissClick() },
                    text = stringResource(id = R.string.dismiss),
                    modifier = Modifier.layoutId(buttonDismissId),
                    defaultHeight = HGButtonDefaults.SmallButtonHeight,
                    leadingIcon = painterResource(id = R.drawable.ic_dismiss)
                )

                HGTextButton(
                    onClick = { onLearnMoreClick() },
                    text = stringResource(id = R.string.learn_more),
                    modifier = Modifier.layoutId(buttonLearnModeId),
                    defaultHeight = HGButtonDefaults.SmallButtonHeight,
                    leadingIcon = painterResource(id = R.drawable.ic_external_link)
                )
            }
        }
    }
}

private fun bannerConstraints(expanded: Boolean): ConstraintSet {
    return ConstraintSet {
        val bannerIcon = createRefFor(bannerIconId)
        val bannerTitle = createRefFor(bannerTitleId)
        val bannerArrow = createRefFor(bannerArrowId)
        val contentBody = createRefFor(bannerBodyId)
        val buttonLearnMore = createRefFor(buttonLearnModeId)
        val buttonDismiss = createRefFor(buttonDismissId)

        constrain(bannerIcon) {
            top.linkTo(parent.top)
            start.linkTo(parent.start)
        }

        constrain(bannerTitle) {
            start.linkTo(bannerIcon.end, 16.dp)
            top.linkTo(bannerIcon.top)
            end.linkTo(bannerArrow.start)
            bottom.linkTo(bannerIcon.bottom)
            width = Dimension.fillToConstraints
        }

        constrain(bannerArrow) {
            top.linkTo(bannerTitle.top)
            end.linkTo(parent.end)
            bottom.linkTo(bannerTitle.bottom)
        }

        constrain(contentBody) {
            start.linkTo(bannerTitle.start)
            top.linkTo(bannerTitle.bottom, 16.dp)
            end.linkTo(parent.end)
            width = Dimension.fillToConstraints
            visibility = if (expanded) {
                Visibility.Visible
            } else {
                Visibility.Gone
            }
        }

        constrain(buttonDismiss) {
            top.linkTo(contentBody.bottom, 16.dp)
            end.linkTo(parent.end)
            bottom.linkTo(parent.bottom)
            visibility = if (expanded) {
                Visibility.Visible
            } else {
                Visibility.Gone
            }
        }

        constrain(buttonLearnMore) {
            top.linkTo(buttonDismiss.top)
            end.linkTo(buttonDismiss.start)
            bottom.linkTo(buttonDismiss.bottom)
            visibility = if (expanded) {
                Visibility.Visible
            } else {
                Visibility.Gone
            }
        }
    }
}

@Composable
@BasePreview
private fun AnnouncementBannerUIPreview() {
    HealthGatewayTheme {
        AnnouncementBannerUI(
            title = stringResource(id = R.string.news_feed),
            description = stringResource(id = R.string.news_feed),
            onDismissClick = {},
            onLearnMoreClick = {}
        )
    }
}

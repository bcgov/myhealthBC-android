package ca.bc.gov.bchealth.compose.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
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

private const val tileIconId = "tileIconId"
private const val tileTitleId = "tileTitleId"
private const val tileArrowId = "tileArrowId"

@Composable
fun QuickAccessTileItemUI(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: Painter,
    title: String
) {
    Card(
        modifier = modifier
            .height(120.dp)
            .clickable { onClick() },
        elevation = 15.dp,
        backgroundColor = MaterialTheme.colors.background
    ) {

        BoxWithConstraints(
            modifier = modifier
                .fillMaxSize()
        ) {
            ConstraintLayout(
                tileConstraints(),
                modifier = modifier
                    .fillMaxSize()
            ) {

                Image(
                    modifier = Modifier
                        .size(32.dp)
                        .layoutId(tileIconId),
                    painter = icon,
                    contentDescription = null
                )

                Text(
                    modifier = Modifier
                        .wrapContentHeight(Alignment.Bottom)
                        .layoutId(tileTitleId),
                    text = title,
                    softWrap = true,
                    style = MaterialTheme.typography.body2,
                    color = MaterialTheme.colors.primary,
                    maxLines = 2
                )

                Image(
                    modifier = Modifier.layoutId(tileArrowId),
                    painter = painterResource(id = R.drawable.ic_right_arrow),
                    contentDescription = null
                )
            }
        }
    }
}

private fun tileConstraints(): ConstraintSet {

    return ConstraintSet {
        val tileIcon = createRefFor(tileIconId)
        val tileTitle = createRefFor(tileTitleId)
        val tileArrow = createRefFor(tileArrowId)

        constrain(tileIcon) {
            start.linkTo(parent.start, 16.dp)
            top.linkTo(parent.top, 16.dp)
        }

        constrain(tileTitle) {
            start.linkTo(parent.start, 16.dp)
            bottom.linkTo(parent.bottom, 16.dp)
            end.linkTo(tileArrow.start, 16.dp)
            width = Dimension.fillToConstraints
        }

        constrain(tileArrow) {
            bottom.linkTo(tileTitle.bottom)
            end.linkTo(parent.end, 16.dp)
        }
    }
}

@Composable
@BasePreview
private fun QuickAccessTileItemUIPreview() {

    HealthGatewayTheme {
        QuickAccessTileItemUI(
            onClick = { /*TODO*/ },
            icon = painterResource(id = R.drawable.ic_tile_healt_resources),
            title = stringResource(id = R.string.health_resources)
        )
    }
}

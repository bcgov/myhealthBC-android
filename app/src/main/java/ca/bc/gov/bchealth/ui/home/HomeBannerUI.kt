package ca.bc.gov.bchealth.ui.home

import android.text.method.LinkMovementMethod
import android.util.TypedValue
import android.widget.TextView
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import ca.bc.gov.bchealth.R
import ca.bc.gov.bchealth.compose.BasePreview
import ca.bc.gov.bchealth.compose.MyHealthTypography
import ca.bc.gov.bchealth.compose.bold
import ca.bc.gov.bchealth.compose.minButtonSize
import ca.bc.gov.bchealth.compose.theme.blue
import ca.bc.gov.bchealth.compose.theme.primaryBlue
import ca.bc.gov.bchealth.ui.custom.DecorativeImage
import ca.bc.gov.bchealth.utils.fromHtml

@Composable
fun BannerUI(
    uiState: BannerItem,
    onClickToggle: () -> Unit,
    onClickLearnMore: (BannerItem) -> Unit,
    onClickDismiss: () -> Unit,
) {

    if (uiState.isHidden) return

    ConstraintLayout(
        Modifier
            .fillMaxWidth()
            .padding(top = 16.dp, bottom = 8.dp, start = 32.dp, end = 32.dp)
            .shadow(elevation = 8.dp, shape = RoundedCornerShape(10.dp))
            .clip(RoundedCornerShape(10.dp))
            .background(Color(0xFFD9EAF7))

    ) {

        val (imgIcon, txtTitle, btToggle, txtBody, btLearnMore, btDismiss, spacer) = createRefs()

        DecorativeImage(
            resourceId = R.drawable.ic_banner_icon,
            modifier = Modifier
                .constrainAs(imgIcon) {
                    start.linkTo(parent.start, margin = 16.dp)
                    linkTo(txtTitle.top, txtTitle.bottom, bias = 0.52f)
                }
        )

        Text(
            text = uiState.title,
            modifier = Modifier
                .constrainAs(txtTitle) {
                    start.linkTo(imgIcon.end, margin = 8.dp)
                    end.linkTo(btToggle.start)
                    top.linkTo(parent.top, margin = 16.dp)
                    width = Dimension.fillToConstraints
                },
            color = blue,
            style = MyHealthTypography.h3,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        val toggleIcon = if (uiState.expanded) {
            R.drawable.ic_content_short
        } else {
            R.drawable.ic_content_full
        }

        Image(
            painter = painterResource(id = toggleIcon),
            contentScale = ContentScale.Inside,
            modifier = Modifier
                .constrainAs(btToggle) {
                    end.linkTo(parent.end)
                    top.linkTo(txtTitle.top)
                    bottom.linkTo(txtTitle.bottom)
                }
                .width(minButtonSize)
                .height(minButtonSize)
                .clickable { onClickToggle.invoke() },
            contentDescription = stringResource(id = R.string.expand_content)
        )

        if (uiState.expanded) {
            Box(
                modifier = Modifier
                    .constrainAs(txtBody) {
                        top.linkTo(txtTitle.bottom, margin = 8.dp)
                        start.linkTo(txtTitle.start)
                        end.linkTo(parent.end, margin = 24.dp)
                        width = Dimension.fillToConstraints
                    }
            ) {
                AndroidView(
                    modifier = Modifier,
                    factory = { context ->
                        TextView(context).apply {
                            setTextAppearance(R.style.HealthGateway_TextAppearance_MaterialComponents_Headline4)
                            setTextSize(TypedValue.COMPLEX_UNIT_SP, 13f)
                        }
                    },
                    update = {
                        it.text = uiState.body.fromHtml().trimEnd().take(120)
                        it.movementMethod = LinkMovementMethod.getInstance()
                    }
                )
            }

            if (uiState.displayReadMore) {
                Row(
                    modifier = Modifier
                        .clickable { onClickLearnMore.invoke(uiState) }
                        .constrainAs(btLearnMore) {
                            top.linkTo(btDismiss.top)
                            end.linkTo(btDismiss.start, margin = 24.dp)
                        },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    DecorativeImage(resourceId = R.drawable.ic_external_link)
                    Text(
                        modifier = Modifier.padding(start = 4.dp, top = 16.dp, bottom = 16.dp),
                        text = stringResource(id = R.string.learn_more).uppercase(),
                        style = MyHealthTypography.h4.bold(),
                        color = primaryBlue,
                        fontSize = 13.sp
                    )
                }
            }

            Row(
                modifier = Modifier
                    .clickable { onClickDismiss.invoke() }
                    .constrainAs(btDismiss) {
                        top.linkTo(txtBody.bottom)
                        end.linkTo(parent.end, margin = 24.dp)
                    },
                verticalAlignment = Alignment.CenterVertically
            ) {
                DecorativeImage(resourceId = R.drawable.ic_dismiss)
                Text(
                    modifier = Modifier.padding(start = 4.dp, top = 16.dp, bottom = 16.dp),
                    text = stringResource(id = R.string.dismiss).uppercase(),
                    style = MyHealthTypography.h4.bold(),
                    color = primaryBlue,
                    fontSize = 13.sp
                )
            }
        }
        Spacer(
            modifier = Modifier
                .constrainAs(spacer) {
                    top.linkTo(txtTitle.bottom, margin = 16.dp)
                }
        )
    }
}

@BasePreview
@Composable
private fun PreviewBannerUI() {
    BannerUI(
        uiState = BannerItem(
            title = "Great news! Really Big Announcement",
            body = "View and manage all your <b>available health records</b>, including dispensed medications, health visits, COVID-19 test results, immunizations and more.",
            date = "",
            displayReadMore = true,
        ),
        onClickToggle = {},
        onClickLearnMore = {},
        onClickDismiss = {}
    )
}

@BasePreview
@Composable
private fun PreviewBannerUICollapsed() {
    BannerUI(
        uiState = BannerItem(
            title = "Great news! Really Big Announcement",
            body = "View and manage all your available health records, including dispensed medications, health visits, COVID-19 test results, immunizations and more.",
            date = "",
            displayReadMore = true,
            expanded = false
        ),
        onClickToggle = {},
        onClickLearnMore = {},
        onClickDismiss = {}
    )
}

@BasePreview
@Composable
private fun PreviewBannerUIWithoutReadMore() {
    BannerUI(
        uiState = BannerItem(
            title = "Great news! Really Big Announcement",
            body = "View and manage all your available health records, including dispensed medications, health visits, COVID-19 test results, immunizations and more.",
            date = "",
            displayReadMore = false,
        ),
        onClickToggle = {},
        onClickLearnMore = {},
        onClickDismiss = {}
    )
}

@BasePreview
@Composable
private fun PreviewBannerUIHidden() {
    BannerUI(
        uiState = BannerItem(
            title = "Great news! Really Big Announcement",
            body = "View and manage all your available health records, including dispensed medications, health visits, COVID-19 test results, immunizations and more.",
            date = "",
            displayReadMore = true,
            isHidden = true,
        ),
        onClickToggle = {},
        onClickLearnMore = {},
        onClickDismiss = {}
    )
}

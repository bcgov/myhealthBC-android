package ca.bc.gov.bchealth.ui.onboarding

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.ConstraintSet
import androidx.constraintlayout.compose.Dimension
import ca.bc.gov.bchealth.R
import ca.bc.gov.bchealth.compose.MultiDevicePreview
import ca.bc.gov.bchealth.compose.theme.m3.HealthGatewayTheme

/**
 * @author pinakin.kansara
 * Created 2023-10-19 at 9:21 a.m.
 */

private const val ONBOARDING_IMAGE_ID = "onboarding_image_id"
private const val TITLE_ID = "title_id"
private const val DESCRIPTION_ID = "description_id"
private const val TXT_NEW_ID = "txt_new_id"

@Composable
fun OnBoardingSliderUI(
    modifier: Modifier = Modifier,
    onBoardingSliderItem: OnBoardingSliderItem,
    isExistingUser: Boolean = false
) {
    BoxWithConstraints {
        val constraint = when {
            maxWidth < 600.dp -> compactConstraint()
            else -> largeConstraint()
        }
        ConstraintLayout(
            constraintSet = constraint,
            modifier = Modifier.fillMaxSize()
        ) {
            Image(
                modifier = Modifier.layoutId(ONBOARDING_IMAGE_ID),
                painter = painterResource(id = onBoardingSliderItem.iconResId),
                contentDescription = null
            )

            AnimatedVisibility(modifier = Modifier.layoutId(TXT_NEW_ID), visible = isExistingUser) {
                Text(
                    text = stringResource(id = R.string.new_string),
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            val titleFontStyle = when {
                maxWidth < 600.dp -> MaterialTheme.typography.headlineSmall
                else -> MaterialTheme.typography.headlineLarge
            }
            Text(
                modifier = Modifier.layoutId(TITLE_ID),
                text = stringResource(id = onBoardingSliderItem.titleResId),
                style = titleFontStyle,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center
            )

            Text(
                modifier = Modifier.layoutId(DESCRIPTION_ID),
                text = stringResource(id = onBoardingSliderItem.descriptionResId),
                style = MaterialTheme.typography.titleMedium,
                textAlign = if (maxWidth < 600.dp) {
                    TextAlign.Center
                } else {
                    TextAlign.Start
                }
            )
        }
    }
}

private fun compactConstraint(): ConstraintSet {
    return ConstraintSet {
        val imageId = createRefFor(ONBOARDING_IMAGE_ID)
        val txtNewId = createRefFor(TXT_NEW_ID)
        val titleId = createRefFor(TITLE_ID)
        val descriptionId = createRefFor(DESCRIPTION_ID)

        constrain(imageId) {
            start.linkTo(parent.start)
            top.linkTo(parent.top)
            end.linkTo(parent.end)
            bottom.linkTo(parent.bottom)
        }

        constrain(txtNewId) {
            start.linkTo(titleId.start)
            bottom.linkTo(titleId.top)
        }
        constrain(titleId) {
            start.linkTo(imageId.start)
            top.linkTo(imageId.bottom, 32.dp)
            end.linkTo(imageId.end)
        }

        constrain(descriptionId) {
            start.linkTo(parent.start, 32.dp)
            top.linkTo(titleId.bottom, 16.dp)
            end.linkTo(parent.end, 32.dp)
            width = Dimension.fillToConstraints
        }
    }
}

private fun largeConstraint(): ConstraintSet {
    return ConstraintSet {
        val imageId = createRefFor(ONBOARDING_IMAGE_ID)
        val txtNewId = createRefFor(TXT_NEW_ID)
        val titleId = createRefFor(TITLE_ID)
        val descriptionId = createRefFor(DESCRIPTION_ID)
        val endGuideline = createGuidelineFromEnd(0.20f)
        val guideline = createGuidelineFromStart(0.40f)

        constrain(imageId) {
            top.linkTo(titleId.top, (-32).dp)
            end.linkTo(guideline, 16.dp)
        }

        constrain(txtNewId) {
            start.linkTo(titleId.start)
            bottom.linkTo(titleId.top)
        }

        constrain(titleId) {
            start.linkTo(guideline, 16.dp)
            bottom.linkTo(descriptionId.top, 32.dp)
        }

        constrain(descriptionId) {
            start.linkTo(guideline, 16.dp)
            end.linkTo(endGuideline)
            bottom.linkTo(parent.bottom)
            width = Dimension.fillToConstraints
        }
    }
}

@MultiDevicePreview
@Composable
private fun OnBoardingSliderUIPreview(
    @PreviewParameter(OnBoardingItemProvider::class) item: OnBoardingSliderItem
) {
    HealthGatewayTheme {
        OnBoardingSliderUI(onBoardingSliderItem = item)
    }
}

internal class OnBoardingItemProvider : PreviewParameterProvider<OnBoardingSliderItem> {

    override val values: Sequence<OnBoardingSliderItem> = sequenceOf(
        OnBoardingSliderItem(
            titleResId = R.string.onboarding_health_records_title,
            descriptionResId = R.string.onboarding_health_records_desc,
            iconResId = R.drawable.ic_onboarding_health_records
        ),
        OnBoardingSliderItem(
            titleResId = R.string.onboarding_services_title,
            descriptionResId = R.string.onboarding_services_desc,
            iconResId = R.drawable.ic_onboarding_services
        )
    )
}

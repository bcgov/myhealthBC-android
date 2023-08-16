package ca.bc.gov.bchealth.ui.recommendations

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Divider
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.ConstraintSet
import androidx.constraintlayout.compose.Dimension
import androidx.constraintlayout.compose.Visibility
import ca.bc.gov.bchealth.R
import ca.bc.gov.bchealth.compose.theme.bannerBackgroundBlue
import ca.bc.gov.bchealth.compose.theme.black
import ca.bc.gov.bchealth.compose.theme.descriptionGrey
import ca.bc.gov.bchealth.compose.theme.disableBackground
import ca.bc.gov.bchealth.compose.theme.grey

private const val ITEM_ICON_ID = "item_icon_id"
private const val ITEM_ARROW_ID = "item_arrow_id"
private const val ITEM_DETAIL_ID = "item_detail_id"
private const val ITEM_PATIENT_NAME_ID = "item_patient_name_id"
private const val ITEM_RECORD_COUNT_ID = "item_record_count_id"

@Composable
fun RecommendationItem(
    modifier: Modifier = Modifier,
    patientWithRecommendations: PatientWithRecommendations,
    expanded: Boolean,
    onArrowClick: () -> Unit
) {
    val hasRecommendations = patientWithRecommendations.recommendations.isNotEmpty()

    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = 15.dp,
        backgroundColor = MaterialTheme.colors.background
    ) {
        BoxWithConstraints(
            modifier = Modifier.fillMaxWidth()
        ) {
            ConstraintLayout(
                recommendationItemConstraints(hasRecommendations),
                modifier = Modifier.fillMaxWidth()
            ) {

                Image(
                    modifier = Modifier
                        .layoutId(ITEM_ICON_ID)
                        .size(48.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(
                            if (hasRecommendations) {
                                bannerBackgroundBlue
                            } else {
                                disableBackground
                            }
                        ),
                    painter = painterResource(
                        id = if (patientWithRecommendations.isDependent) {
                            R.drawable.ic_manage_dependent
                        } else {
                            R.drawable.ic_profile
                        }
                    ),
                    contentScale = ContentScale.None,
                    colorFilter = ColorFilter.tint(
                        if (hasRecommendations) {
                            MaterialTheme.colors.primary
                        } else {
                            grey
                        }
                    ),
                    contentDescription = null
                )

                Text(
                    modifier = Modifier.layoutId(ITEM_PATIENT_NAME_ID).clickable {
                        if (hasRecommendations) {
                            onArrowClick()
                        }
                    },
                    text = patientWithRecommendations.name ?: "",
                    style = MaterialTheme.typography.body1,
                    fontWeight = FontWeight.Bold,
                    color = if (!hasRecommendations) {
                        descriptionGrey
                    } else {
                        black
                    }
                )

                Text(
                    modifier = Modifier.layoutId(ITEM_RECORD_COUNT_ID),
                    text = "${patientWithRecommendations.recommendations.size}",
                    style = MaterialTheme.typography.body1,
                    fontWeight = FontWeight.Bold,
                    color = if (!hasRecommendations) {
                        descriptionGrey
                    } else {
                        MaterialTheme.colors.primary
                    }
                )

                val toggleIcon = if (expanded) {
                    R.drawable.ic_content_short
                } else {
                    R.drawable.ic_content_full
                }

                IconButton(
                    onClick = {
                        if (hasRecommendations) {
                            onArrowClick()
                        }
                    },
                    modifier = Modifier
                        .layoutId(ITEM_ARROW_ID)
                ) {
                    Image(
                        painter = painterResource(id = toggleIcon),
                        contentDescription = null
                    )
                }

                AnimatedVisibility(
                    modifier = Modifier.layoutId(ITEM_DETAIL_ID),
                    visible = expanded
                ) {

                    LazyColumn(
                        modifier = Modifier.heightIn(max = (patientWithRecommendations.recommendations.size * 112).dp),
                        userScrollEnabled = false
                    ) {
                        item {
                            Divider()
                        }
                        items(patientWithRecommendations.recommendations) {
                            RecommendationDetailItem(recommendationDetailItem = it)
                        }
                    }
                }
            }
        }
    }
}

private fun recommendationItemConstraints(hasRecommendations: Boolean) = ConstraintSet {
    val itemIconId = createRefFor(ITEM_ICON_ID)
    val itemArrow = createRefFor(ITEM_ARROW_ID)
    val itemDetailId = createRefFor(ITEM_DETAIL_ID)
    val itemPatientNameId = createRefFor(ITEM_PATIENT_NAME_ID)
    val itemRecordCountId = createRefFor(ITEM_RECORD_COUNT_ID)

    constrain(itemIconId) {
        start.linkTo(parent.start, 16.dp)
        top.linkTo(parent.top, 16.dp)
    }

    constrain(itemPatientNameId) {
        start.linkTo(itemIconId.end, 16.dp)
        top.linkTo(itemIconId.top)
        bottom.linkTo(itemIconId.bottom)
        end.linkTo(itemRecordCountId.start)
        width = Dimension.fillToConstraints
    }

    constrain(itemRecordCountId) {
        top.linkTo(itemArrow.top)
        end.linkTo(itemArrow.start)
        bottom.linkTo(itemArrow.bottom)
    }

    constrain(itemArrow) {
        top.linkTo(parent.top, 16.dp)
        end.linkTo(parent.end, 16.dp)
        visibility = if (hasRecommendations) {
            Visibility.Visible
        } else {
            Visibility.Invisible
        }
    }

    constrain(itemDetailId) {
        top.linkTo(itemIconId.bottom, 16.dp)
    }
}

package ca.bc.gov.bchealth.ui.recommendations

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.ConstraintSet
import androidx.constraintlayout.compose.Dimension
import ca.bc.gov.bchealth.R
import ca.bc.gov.bchealth.compose.BasePreview
import ca.bc.gov.bchealth.compose.theme.HealthGatewayTheme
import ca.bc.gov.bchealth.compose.theme.bannerBackgroundBlue
import ca.bc.gov.bchealth.compose.theme.descriptionGrey
import ca.bc.gov.bchealth.compose.theme.grey
import ca.bc.gov.bchealth.compose.theme.statusBlue

@Composable
fun RecommendationScreen(modifier: Modifier = Modifier) {

    RecommendationScreenContent(modifier)
}

@Composable
private fun RecommendationScreenContent(
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(32.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        item {
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = stringResource(id = R.string.recommendations_para1),
                style = MaterialTheme.typography.body2,
                color = grey
            )
        }
        item {
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = stringResource(id = R.string.recommendations_para2),
                style = MaterialTheme.typography.body2,
                color = grey
            )
        }

        for (i in 0 until 5) {
            item {
                RecommendationItem()
            }
        }
    }
}

@Composable
private fun RecommendationItem(modifier: Modifier = Modifier) {
    var expanded by remember { mutableStateOf(true) }
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = 15.dp,
        backgroundColor = MaterialTheme.colors.background
    ) {
        BoxWithConstraints(
            modifier = Modifier.fillMaxWidth()
        ) {
            ConstraintLayout(
                recommendationItemConstraints(),
                modifier = Modifier.fillMaxWidth()
            ) {

                Image(
                    modifier = Modifier
                        .layoutId(ITEM_ICON_ID)
                        .size(48.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(
                            bannerBackgroundBlue
                        ),
                    painter = painterResource(id = R.drawable.ic_banner_icon),
                    contentScale = ContentScale.None,
                    contentDescription = null
                )

                Text(
                    modifier = Modifier.layoutId(ITEM_PATIENT_NAME_ID),
                    text = stringResource(id = R.string.home),
                    style = MaterialTheme.typography.body1,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    modifier = Modifier.layoutId(ITEM_RECORD_COUNT_ID),
                    text = "3",
                    style = MaterialTheme.typography.body1,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colors.primary
                )

                val toggleIcon = if (expanded) {
                    R.drawable.ic_content_short
                } else {
                    R.drawable.ic_content_full
                }

                IconButton(
                    onClick = { expanded = !expanded },
                    modifier = Modifier
                        .layoutId(ITEM_ARROW_ID)
                ) {
                    Image(
                        painter = painterResource(id = toggleIcon),
                        contentDescription = null
                    )
                }

                AnimatedVisibility(modifier = Modifier.layoutId(ITEM_DETAIL_ID), visible = expanded) {

                    LazyColumn(
                        modifier = Modifier.heightIn(max = (5 * 80).dp),
                        userScrollEnabled = false
                    ) {
                        item {
                            Divider()
                        }
                        items(5) {
                            RecommendationDetailItem()
                        }
                    }
                }
            }
        }
    }
}

private const val ITEM_ICON_ID = "item_icon_id"
private const val ITEM_ARROW_ID = "item_arrow_id"
private const val ITEM_DETAIL_ID = "item_detail_id"
private const val ITEM_PATIENT_NAME_ID = "item_patient_name_id"
private const val ITEM_RECORD_COUNT_ID = "item_record_count_id"
private fun recommendationItemConstraints() = ConstraintSet {
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
    }

    constrain(itemDetailId) {
        top.linkTo(itemIconId.bottom, 16.dp)
    }
}

@Composable
private fun RecommendationDetailItem(modifier: Modifier = Modifier) {
    BoxWithConstraints(modifier = Modifier.background(MaterialTheme.colors.background)) {
        ConstraintLayout(
            modifier = Modifier.fillMaxWidth(),
            constraintSet = recommendationDetailItemConstraint()
        ) {
            Text(
                modifier = Modifier.layoutId(ITEM_VACCINE_NAME_ID),
                text = "Yello Fever",
                style = MaterialTheme.typography.body1,
                fontWeight = FontWeight.Bold,
                color = statusBlue
            )
            Icon(
                modifier = Modifier
                    .size(16.dp)
                    .layoutId(ITEM_ICON_ID),
                painter = painterResource(id = R.drawable.ic_recommendation),
                tint = MaterialTheme.colors.primary,
                contentDescription = null
            )
            Text(
                modifier = Modifier.layoutId(ITEM_STATUS_ID),
                text = "Yello Fever",
                style = MaterialTheme.typography.body2,
                color = descriptionGrey
            )
            Text(
                modifier = Modifier.layoutId(ITEM_DATE_ID),
                text = "Yello Fever",
                style = MaterialTheme.typography.body2,
                color = descriptionGrey
            )
        }
    }
}

private const val ITEM_VACCINE_NAME_ID = "item_vaccine_name_id"
private const val ITEM_STATUS_ID = "item_status_id"
private const val ITEM_DATE_ID = "item_date_id"
private fun recommendationDetailItemConstraint() = ConstraintSet {
    val itemVaccineNameId = createRefFor(ITEM_VACCINE_NAME_ID)
    val itemStatusId = createRefFor(ITEM_STATUS_ID)
    val itemDateId = createRefFor(ITEM_DATE_ID)
    val itemIconId = createRefFor(ITEM_ICON_ID)

    constrain(itemVaccineNameId) {
        start.linkTo(parent.start, 16.dp)
        top.linkTo(parent.top, 16.dp)
    }

    constrain(itemIconId) {
        start.linkTo(itemVaccineNameId.end, 16.dp)
        top.linkTo(itemVaccineNameId.top)
        bottom.linkTo(itemVaccineNameId.bottom)
    }

    constrain(itemStatusId) {
        start.linkTo(itemVaccineNameId.start)
        top.linkTo(itemVaccineNameId.bottom)
    }

    constrain(itemDateId) {
        start.linkTo(itemStatusId.start)
        top.linkTo(itemStatusId.bottom)
    }
}

@Composable
@Preview(showBackground = false)
private fun RecommendationItemPreview() {

    HealthGatewayTheme {
        RecommendationItem()
    }
}

@BasePreview
@Composable
private fun RecommendationScreenPreview() {

    HealthGatewayTheme {
        RecommendationScreen()
    }
}

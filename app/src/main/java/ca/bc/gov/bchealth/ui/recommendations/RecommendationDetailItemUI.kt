package ca.bc.gov.bchealth.ui.recommendations

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.ConstraintSet
import androidx.constraintlayout.compose.Dimension
import ca.bc.gov.bchealth.compose.BasePreview
import ca.bc.gov.bchealth.compose.theme.HealthGatewayTheme
import ca.bc.gov.bchealth.compose.theme.descriptionGrey
import ca.bc.gov.bchealth.compose.theme.statusBlue
import ca.bc.gov.common.model.immunization.ForecastStatus

private const val ITEM_VACCINE_NAME_ID = "item_vaccine_name_id"
private const val ITEM_STATUS_ID = "item_status_id"
private const val ITEM_DATE_ID = "item_date_id"
private const val ITEM_ICON_ID = "item_icon_id"

@Composable
fun RecommendationDetailItem(
    modifier: Modifier = Modifier,
    recommendationDetailItem: RecommendationDetailItem
) {
    BoxWithConstraints(modifier) {
        ConstraintLayout(
            modifier = Modifier.fillMaxWidth(),
            constraintSet = recommendationDetailItemConstraint()
        ) {
            Text(
                modifier = Modifier
                    .layoutId(ITEM_VACCINE_NAME_ID),
                text = recommendationDetailItem.title,
                style = MaterialTheme.typography.body1,
                fontWeight = FontWeight.Bold,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                color = statusBlue
            )

            Text(
                modifier = Modifier.layoutId(ITEM_STATUS_ID),
                text = recommendationDetailItem.status?.text ?: "",
                style = MaterialTheme.typography.body2,
                color = descriptionGrey
            )
            Text(
                modifier = Modifier.layoutId(ITEM_DATE_ID),
                text = recommendationDetailItem.date,
                style = MaterialTheme.typography.body2,
                color = descriptionGrey
            )
        }
    }
}

private fun recommendationDetailItemConstraint() = ConstraintSet {
    val itemVaccineNameId = createRefFor(ITEM_VACCINE_NAME_ID)
    val itemStatusId = createRefFor(ITEM_STATUS_ID)
    val itemDateId = createRefFor(ITEM_DATE_ID)
    val itemIconId = createRefFor(ITEM_ICON_ID)

    constrain(itemVaccineNameId) {
        start.linkTo(parent.start, 16.dp)
        end.linkTo(parent.end, 16.dp)
        top.linkTo(parent.top, 16.dp)
        width = Dimension.fillToConstraints
    }

    constrain(itemStatusId) {
        start.linkTo(itemVaccineNameId.start)
        top.linkTo(itemVaccineNameId.bottom)
    }

    constrain(itemDateId) {
        start.linkTo(itemStatusId.start)
        top.linkTo(itemStatusId.bottom)
        bottom.linkTo(parent.bottom, 16.dp)
    }
}

@Composable
@BasePreview
private fun RecommendationDetailItemPreview() {
    HealthGatewayTheme {
        RecommendationDetailItem(
            recommendationDetailItem = RecommendationDetailItem(
                title = "test",
                date = "date",
                status = ForecastStatus.getByText("Test")
            )
        )
    }
}

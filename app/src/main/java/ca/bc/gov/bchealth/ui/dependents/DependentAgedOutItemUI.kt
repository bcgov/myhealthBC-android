package ca.bc.gov.bchealth.ui.dependents

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.ConstraintSet
import androidx.constraintlayout.compose.Dimension
import ca.bc.gov.bchealth.R
import ca.bc.gov.bchealth.compose.BasePreview
import ca.bc.gov.bchealth.compose.component.HGOutlinedButton
import ca.bc.gov.bchealth.compose.theme.HealthGatewayTheme
import ca.bc.gov.bchealth.compose.theme.lightGrey
import ca.bc.gov.bchealth.compose.theme.warningColor
import ca.bc.gov.bchealth.compose.theme.warningText

/**
 * @author pinakin.kansara
 * Created 2023-10-24 at 12:20 p.m.
 */

private const val IMG_LEADING_ICON_ID = "img_leading_icon_id"
private const val TXT_WARNING_ID = "txt_warning_id"

@Composable
fun DependentAgedOutItemUI(
    onRemoveDependentClick: () -> Unit,
    modifier: Modifier = Modifier,
    title: String
) {
    Card(
        modifier = Modifier
            .fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        backgroundColor = lightGrey,
        elevation = 0.dp,
    ) {

        Column(
            modifier = modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = title,
                style = MaterialTheme.typography.body1,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colors.primary
            )
            Spacer(
                modifier = Modifier
                    .height(16.dp)
                    .fillMaxWidth()
            )
            DependentAgedOutWarningUI()
            Spacer(
                modifier = Modifier
                    .height(16.dp)
                    .fillMaxWidth()
            )
            HGOutlinedButton(
                modifier = Modifier.fillMaxWidth(),
                onClick = onRemoveDependentClick,
                text = stringResource(id = R.string.dependent_remove)
            )
        }
    }
}

@Composable
private fun DependentAgedOutWarningUI() {
    BoxWithConstraints(
        modifier = Modifier
            .background(warningColor)
            .padding(16.dp)
            .fillMaxWidth()
    ) {
        ConstraintLayout(
            modifier = Modifier.fillMaxWidth(),
            constraintSet = constraints()
        ) {
            Image(
                modifier = Modifier.layoutId(IMG_LEADING_ICON_ID),
                painter = painterResource(id = R.drawable.ic_warning),
                contentDescription = ""
            )
            Text(
                modifier = Modifier.layoutId(TXT_WARNING_ID),
                text = stringResource(id = R.string.dependents_aged_out_message),
                style = MaterialTheme.typography.body1,
                color = warningText
            )
        }
    }
}

private fun constraints(): ConstraintSet {
    return ConstraintSet {
        val leadingImageId = createRefFor(IMG_LEADING_ICON_ID)
        val txtWarningId = createRefFor(TXT_WARNING_ID)

        constrain(leadingImageId) {
            start.linkTo(parent.start)
            top.linkTo(parent.top)
        }

        constrain(txtWarningId) {
            start.linkTo(leadingImageId.end, 16.dp)
            top.linkTo(leadingImageId.top)
            end.linkTo(parent.end)
            width = Dimension.fillToConstraints
        }
    }
}

@BasePreview
@Composable
private fun DependentAgedOutItemUIPreview() {
    HealthGatewayTheme {
        DependentAgedOutItemUI(onRemoveDependentClick = {}, title = "Hello world")
    }
}

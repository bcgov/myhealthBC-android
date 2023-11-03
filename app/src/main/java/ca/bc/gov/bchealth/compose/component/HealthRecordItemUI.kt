package ca.bc.gov.bchealth.compose.component

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import ca.bc.gov.bchealth.R
import ca.bc.gov.bchealth.compose.BasePreview
import ca.bc.gov.bchealth.compose.smallButton
import ca.bc.gov.bchealth.compose.theme.HealthGatewayTheme
import ca.bc.gov.bchealth.compose.theme.blue
import ca.bc.gov.bchealth.compose.theme.grey
import ca.bc.gov.bchealth.compose.theme.lightGrey

@Composable
fun HealthRecordItemUI(
    onClick: () -> Unit,
    @DrawableRes image: Int,
    title: String,
    description: String
) {
    Card(
        modifier = Modifier
            .clickable { onClick() }
            .fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        backgroundColor = lightGrey,
        elevation = 0.dp,
    ) {

        ConstraintLayout(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            val (icon, txtTitle, txtDescription, rightArrowImage) = createRefs()
            Image(
                modifier = Modifier.constrainAs(icon) {
                    start.linkTo(parent.start)
                    top.linkTo(parent.top)
                },
                painter = painterResource(id = image),
                contentDescription = null
            )

            Text(
                modifier = Modifier.constrainAs(txtTitle) {
                    top.linkTo(icon.top)
                    start.linkTo(icon.end, margin = 16.dp)
                    end.linkTo(rightArrowImage.start)
                    width = Dimension.fillToConstraints
                },
                text = title,
                style = MaterialTheme.typography.subtitle2,
                color = blue,
                maxLines = 2,
                fontWeight = FontWeight.Bold
            )
            Text(
                modifier = Modifier.constrainAs(txtDescription) {
                    start.linkTo(txtTitle.start)
                    top.linkTo(txtTitle.bottom)
                    end.linkTo(txtTitle.end)
                    bottom.linkTo(parent.bottom, margin = 16.dp)
                    width = Dimension.fillToConstraints
                },
                text = description,
                style = MaterialTheme.typography.smallButton,
                color = grey,
                fontWeight = FontWeight.Normal,
                maxLines = 2
            )

            Image(
                modifier = Modifier.constrainAs(rightArrowImage) {
                    end.linkTo(parent.end)
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                },
                painter = painterResource(id = R.drawable.ic_angle_right),
                contentDescription = null,
                alignment = Alignment.Center
            )
        }
    }
}

@BasePreview
@Composable
private fun HealthRecordUIPreview() {
    HealthGatewayTheme {
        HealthRecordItemUI(
            onClick = {},
            image = R.drawable.ic_resources_how_to_get_vax,
            "Big Title with lot of text that goes off the screen",
            "Description"
        )
    }
}

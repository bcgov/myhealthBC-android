package ca.bc.gov.bchealth.ui.inappupdate

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ChainStyle
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.ConstraintSet
import androidx.constraintlayout.compose.Dimension
import ca.bc.gov.bchealth.R
import ca.bc.gov.bchealth.compose.BasePreview
import ca.bc.gov.bchealth.compose.component.m3.HGButton
import ca.bc.gov.bchealth.compose.theme.m3.HealthGatewayTheme

/**
 * @author pinakin.kansara
 * Created 2023-10-20 at 12:51 p.m.
 */

private const val IMG_APP_LOGO_ID = "img_app_logo_id"
private const val TXT_BODY_ID = "txt_body_id"
private const val BTN_UPDATE_ID = "btn_update_id"
private const val IMG_BRAND_IMAGE_ID = "img_brand_image_id"
private const val TXT_FOOTER_TEXT_ID = "txt_footer_text_id"

@Composable
fun InAppUpdateScreen(onNavigate: () -> Unit, modifier: Modifier = Modifier) {

    InAppUpdateScreenContent(onNavigate, modifier)
}

@Composable
private fun InAppUpdateScreenContent(onUpdateNow: () -> Unit, modifier: Modifier = Modifier) {
    BoxWithConstraints {
        ConstraintLayout(
            modifier = modifier
                .background(color = MaterialTheme.colorScheme.primary)
                .fillMaxSize(),
            constraintSet = constraint()
        ) {

            Image(
                modifier = Modifier.layoutId(IMG_APP_LOGO_ID),
                painter = painterResource(id = R.drawable.ic_splash_logo),
                contentDescription = null
            )

            Text(
                modifier = Modifier.layoutId(TXT_BODY_ID),
                text = stringResource(id = R.string.in_app_update_body),
                color = MaterialTheme.colorScheme.background,
                textAlign = TextAlign.Center
            )

            HGButton(
                onClick = onUpdateNow,
                modifier = Modifier
                    .layoutId(BTN_UPDATE_ID)
                    .padding(top = 32.dp),
                text = stringResource(id = R.string.in_app_update_button),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    contentColor = MaterialTheme.colorScheme.primary
                )
            )

            Text(
                modifier = Modifier.layoutId(TXT_FOOTER_TEXT_ID),
                text = stringResource(id = R.string.in_app_update_footer),
                color = MaterialTheme.colorScheme.background,
                textAlign = TextAlign.Center
            )

            Image(
                modifier = Modifier.layoutId(IMG_BRAND_IMAGE_ID),
                painter = painterResource(id = R.drawable.ic_bc_app_logo),
                contentDescription = null
            )
        }
    }
}

private fun constraint(): ConstraintSet {
    return ConstraintSet {
        val imgAppLogoId = createRefFor(IMG_APP_LOGO_ID)
        val txtBodyId = createRefFor(TXT_BODY_ID)
        val btnUpdateNowId = createRefFor(BTN_UPDATE_ID)
        val txtFooterId = createRefFor(TXT_FOOTER_TEXT_ID)
        val imgBrandId = createRefFor(IMG_BRAND_IMAGE_ID)
        val chain = createVerticalChain(
            imgAppLogoId,
            txtBodyId,
            btnUpdateNowId,
            chainStyle = ChainStyle.Packed
        )
        constrain(chain) {
            top.linkTo(parent.top)
            bottom.linkTo(txtFooterId.top)
        }

        constrain(imgAppLogoId) {
            start.linkTo(parent.start)
            top.linkTo(parent.top)
            end.linkTo(parent.end)
        }
        constrain(txtBodyId) {
            start.linkTo(parent.start, 32.dp)
            top.linkTo(imgAppLogoId.bottom)
            end.linkTo(parent.end, 32.dp)
            bottom.linkTo(btnUpdateNowId.top)
            width = Dimension.fillToConstraints
        }
        constrain(btnUpdateNowId) {
            start.linkTo(parent.start)
            top.linkTo(txtBodyId.bottom, 32.dp)
            end.linkTo(parent.end)
        }
        constrain(txtFooterId) {
            start.linkTo(parent.start, 32.dp)
            end.linkTo(parent.end, 32.dp)
            bottom.linkTo(imgBrandId.top, 32.dp)
            width = Dimension.fillToConstraints
        }
        constrain(imgBrandId) {
            start.linkTo(parent.start)
            end.linkTo(parent.end)
            bottom.linkTo(parent.bottom, 32.dp)
        }
    }
}

@BasePreview
@Composable
private fun InAppUpdateScreenPreview() {
    HealthGatewayTheme {
        InAppUpdateScreenContent(onUpdateNow = {})
    }
}

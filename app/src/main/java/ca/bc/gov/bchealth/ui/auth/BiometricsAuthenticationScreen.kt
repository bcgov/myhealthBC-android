package ca.bc.gov.bchealth.ui.auth

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ChainStyle
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.ConstraintSet
import androidx.constraintlayout.compose.Dimension
import ca.bc.gov.bchealth.R
import ca.bc.gov.bchealth.compose.BasePreview
import ca.bc.gov.bchealth.compose.component.m3.HGButton
import ca.bc.gov.bchealth.compose.component.m3.HGTextButton
import ca.bc.gov.bchealth.compose.theme.m3.HealthGatewayTheme

/**
 * @author pinakin.kansara
 * Created 2023-10-20 at 2:52 p.m.
 */

private const val TXT_TITLE_ID = "txt_title_id"
private const val TXT_DESCRIPTION_ID = "txt_description_id"
private const val IMG_BIOMETRIC_ID = "img_biometric_id"
private const val BTN_LEARN_MORE_ID = "btn_learn_more_id"
private const val BTN_USE_BIOMETRIC_ID = "btn_use_biometric_id"

@Composable
fun BiometricsAuthenticationScreen(modifier: Modifier = Modifier) {

    BiometricsAuthenticationScreenContent(modifier)
}

@Composable
private fun BiometricsAuthenticationScreenContent(
    modifier: Modifier = Modifier
) {

    val biometricAuthenticator = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult(),
        onResult = {
        }
    )

    BoxWithConstraints {
        val constraints = when {
            maxWidth < 600.dp -> compactConstraint()
            else -> largeScreenConstraints()
        }
        ConstraintLayout(
            constraintSet = constraints,
            modifier = modifier.fillMaxSize()
        ) {
            val titleFontStyle = when {
                maxWidth < 600.dp -> MaterialTheme.typography.headlineSmall
                else -> MaterialTheme.typography.headlineLarge
            }

            Text(
                modifier = Modifier.layoutId(TXT_TITLE_ID),
                text = stringResource(id = R.string.tv_biometric_heading),
                style = titleFontStyle,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            Text(
                modifier = Modifier.layoutId(TXT_DESCRIPTION_ID),
                text = stringResource(id = R.string.biometric_description),
                style = MaterialTheme.typography.titleMedium,
                textAlign = if (maxWidth < 600.dp) {
                    TextAlign.Center
                } else {
                    TextAlign.Start
                }
            )

            Image(
                modifier = Modifier.layoutId(IMG_BIOMETRIC_ID),
                painter = painterResource(id = R.drawable.ic_biometric),
                contentDescription = null
            )

            HGTextButton(
                onClick = { },
                modifier = Modifier.layoutId(BTN_LEARN_MORE_ID),
                text = stringResource(id = R.string.biometric_learn_more)
            )

            HGButton(
                onClick = { },
                modifier = Modifier.layoutId(BTN_USE_BIOMETRIC_ID),
                text = stringResource(id = R.string.btn_use_biometrics)
            )
        }
    }
}

private fun compactConstraint(): ConstraintSet {
    return ConstraintSet {
        val txtTitleId = createRefFor(TXT_TITLE_ID)
        val imgBiometricId = createRefFor(IMG_BIOMETRIC_ID)
        val txtDescriptionId = createRefFor(TXT_DESCRIPTION_ID)
        val btnLearnMoreId = createRefFor(BTN_LEARN_MORE_ID)
        val btnUseBiometricId = createRefFor(BTN_USE_BIOMETRIC_ID)

        val startGuideLine = createGuidelineFromStart(32.dp)
        val endGuideline = createGuidelineFromEnd(32.dp)
        constrain(txtTitleId) {
            start.linkTo(startGuideLine)
            top.linkTo(parent.top, 32.dp)
            end.linkTo(endGuideline)
            width = Dimension.fillToConstraints
        }
        constrain(txtDescriptionId) {
            start.linkTo(startGuideLine)
            top.linkTo(txtTitleId.bottom, 32.dp)
            end.linkTo(endGuideline)
            width = Dimension.fillToConstraints
        }

        val chain =
            createVerticalChain(imgBiometricId, btnLearnMoreId, chainStyle = ChainStyle.Packed)
        constrain(chain) {
            top.linkTo(txtDescriptionId.bottom)
            bottom.linkTo(btnUseBiometricId.top)
        }
        constrain(imgBiometricId) {
            start.linkTo(startGuideLine)
            end.linkTo(endGuideline)
            top.linkTo(txtDescriptionId.bottom)
        }
        constrain(btnLearnMoreId) {
            start.linkTo(startGuideLine, 32.dp)
            top.linkTo(imgBiometricId.bottom)
            end.linkTo(endGuideline, 32.dp)
            width = Dimension.fillToConstraints
        }

        constrain(btnUseBiometricId) {
            start.linkTo(startGuideLine)
            end.linkTo(endGuideline)
            bottom.linkTo(parent.bottom, 32.dp)
        }
    }
}

private fun largeScreenConstraints(): ConstraintSet {
    return ConstraintSet {
        val txtTitleId = createRefFor(TXT_TITLE_ID)
        val imgBiometricId = createRefFor(IMG_BIOMETRIC_ID)
        val txtDescriptionId = createRefFor(TXT_DESCRIPTION_ID)
        val btnLearnMoreId = createRefFor(BTN_LEARN_MORE_ID)
        val btnUseBiometricId = createRefFor(BTN_USE_BIOMETRIC_ID)

        val startGuideLine = createGuidelineFromStart(0.2f)

        val endGuideline = createGuidelineFromEnd(0.2f)
    }
}

@BasePreview
@Composable
private fun BiometricsAuthenticationScreenPreview() {

    HealthGatewayTheme {
        BiometricsAuthenticationScreenContent()
    }
}

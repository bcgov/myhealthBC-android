package ca.bc.gov.bchealth.ui.screen.biometric

import android.content.Intent
import android.hardware.biometrics.BiometricManager.Authenticators.BIOMETRIC_STRONG
import android.hardware.biometrics.BiometricManager.Authenticators.BIOMETRIC_WEAK
import android.hardware.biometrics.BiometricManager.Authenticators.DEVICE_CREDENTIAL
import android.os.Build
import android.provider.Settings
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ChainStyle
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.ConstraintSet
import androidx.constraintlayout.compose.Dimension
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import ca.bc.gov.bchealth.R
import ca.bc.gov.bchealth.compose.MultiDevicePreview
import ca.bc.gov.bchealth.compose.component.m3.HGButton
import ca.bc.gov.bchealth.compose.component.m3.HGErrorDialog
import ca.bc.gov.bchealth.compose.component.m3.HGTextButton
import ca.bc.gov.bchealth.compose.theme.m3.HealthGatewayTheme
import ca.bc.gov.bchealth.ui.auth.BioMetricState
import java.util.concurrent.Executor

/**
 * @author pinakin.kansara
 * Created 2023-10-20 at 2:52 p.m.
 */

private const val TXT_TITLE_ID = "txt_title_id"
private const val TXT_DESCRIPTION_ID = "txt_description_id"
private const val IMG_BIOMETRIC_ID = "img_biometric_id"
private const val BTN_LEARN_MORE_ID = "btn_learn_more_id"
private const val BTN_USE_BIOMETRIC_ID = "btn_use_biometric_id"

private const val AUTHENTICATORS = BIOMETRIC_STRONG or BIOMETRIC_WEAK or DEVICE_CREDENTIAL
private const val BIOMETRIC_TAG = "Biometric"

@Composable
fun BiometricsAuthenticationScreen(
    onBiometricResult: (BioMetricState) -> Unit,
    onLearnMoreClick: () -> Unit,
    modifier: Modifier = Modifier
) {

    val context = LocalContext.current
    BackHandler {
        val activity = context as FragmentActivity
        activity.finishAndRemoveTask()
    }
    val executor = remember { ContextCompat.getMainExecutor(context) }

    val biometricPrompt = getBioMetricPrompt(
        onError = {
            onBiometricResult(BioMetricState.FAILED)
        },
        onSuccess = {
            onBiometricResult(BioMetricState.SUCCESS)
        },
        context as FragmentActivity,
        executor
    )
    val promptInfo = getPromptInfo(
        stringResource(id = R.string.app_name),
        stringResource(id = R.string.dialog_biometric_description)
    )
    val biometricManager = BiometricManager.from(context)

    val biometricAuthenticator = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult(),
        onResult = {
            Log.d(BIOMETRIC_TAG, "result code = ${it.resultCode}")
        }
    )

    val showNoHardwareDialog = remember { mutableStateOf(false) }
    val showAuthenticationErrorDialog = remember { mutableStateOf(false) }
    val showUserNotEnrolledDialog = remember { mutableStateOf(false) }

    if (showNoHardwareDialog.value) {
        HGErrorDialog(
            onPositiveBtnClick = { showNoHardwareDialog.value = false },
            onNegativeBtnClick = { /*TODO*/ },
            title = stringResource(id = R.string.error_biometric_authentication_title),
            message = stringResource(id = R.string.error_biometric_unknown),
            positiveBtnLabel = stringResource(id = R.string.btn_ok),
            negativeBtnLabel = null
        )
    }

    if (showAuthenticationErrorDialog.value) {
        HGErrorDialog(
            onPositiveBtnClick = { showAuthenticationErrorDialog.value = false },
            onNegativeBtnClick = { /*TODO*/ },
            title = stringResource(id = R.string.error_biometric_authentication_title),
            message = stringResource(id = R.string.error_biometric_no_hardware),
            positiveBtnLabel = stringResource(id = R.string.btn_ok),
            negativeBtnLabel = null
        )
    }

    if (showUserNotEnrolledDialog.value) {
        HGErrorDialog(
            onPositiveBtnClick = {
                showUserNotEnrolledDialog.value = false
                biometricAuthenticator.launch(getSettingsIntent())
            },
            onNegativeBtnClick = {
                showUserNotEnrolledDialog.value = false
            },
            title = stringResource(id = R.string.error_biometric_enrollment_title),
            message = stringResource(id = R.string.error_biometric_enrollment_message),
            positiveBtnLabel = stringResource(id = R.string.dialog_btn_settings),
            negativeBtnLabel = stringResource(id = R.string.dialog_btn_not_now)
        )
    }

    BiometricsAuthenticationScreenContent(
        onLearnMoreClick = onLearnMoreClick,
        onUseBiometricsClick = {
            when (biometricManager.canAuthenticate(AUTHENTICATORS)) {
                BiometricManager.BIOMETRIC_SUCCESS -> {
                    biometricPrompt.authenticate(promptInfo)
                }

                BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> {
                    showUserNotEnrolledDialog.value = true
                }

                BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> {
                    showNoHardwareDialog.value = true
                }

                else -> {
                    showAuthenticationErrorDialog.value = true
                }
            }
        },
        modifier
    )
}

private fun getSettingsIntent(): Intent? {
    val enrollIntent: Intent?
    when {
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.R -> {
            enrollIntent = Intent(Settings.ACTION_BIOMETRIC_ENROLL).apply {
                putExtra(
                    Settings.EXTRA_BIOMETRIC_AUTHENTICATORS_ALLOWED,
                    AUTHENTICATORS
                )
            }
        }

        Build.VERSION.SDK_INT < Build.VERSION_CODES.R && Build.VERSION.SDK_INT >= Build.VERSION_CODES.P -> {
            enrollIntent = Intent(Settings.ACTION_FINGERPRINT_ENROLL)
        }

        else -> {
            enrollIntent = Intent(Settings.ACTION_SECURITY_SETTINGS)
        }
    }
    return enrollIntent
}

private fun getPromptInfo(title: String, subTitle: String) = BiometricPrompt.PromptInfo.Builder()
    .setTitle(title)
    .setSubtitle(subTitle)
    .setAllowedAuthenticators(AUTHENTICATORS)
    .build()

private fun getBioMetricPrompt(
    onError: () -> Unit,
    onSuccess: () -> Unit,
    activity: FragmentActivity,
    executor: Executor
): BiometricPrompt {
    return BiometricPrompt(
        activity,
        executor,
        object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                super.onAuthenticationError(errorCode, errString)
                when (errorCode) {
                    BiometricPrompt.ERROR_USER_CANCELED -> {
                        // no implementation required
                    }

                    else -> {
                        val errorMessage = "$errorCode, $errString"
                        onError()
                    }
                }
            }

            override fun onAuthenticationFailed() {
                super.onAuthenticationFailed()
            }

            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                super.onAuthenticationSucceeded(result)
                onSuccess()
            }
        }
    )
}

@Composable
private fun BiometricsAuthenticationScreenContent(
    onLearnMoreClick: () -> Unit,
    onUseBiometricsClick: () -> Unit,
    modifier: Modifier = Modifier
) {

    BoxWithConstraints {
        val constraints = when {
            maxWidth < 600.dp -> compactConstraint()
            maxWidth < 840.dp -> mediumConstraint()
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
                style = MaterialTheme.typography.titleMedium
            )

            Image(
                modifier = Modifier.layoutId(IMG_BIOMETRIC_ID),
                painter = painterResource(id = R.drawable.ic_biometric),
                contentDescription = null
            )

            HGTextButton(
                onClick = onLearnMoreClick,
                modifier = Modifier.layoutId(BTN_LEARN_MORE_ID)
            ) {
                Text(
                    text = stringResource(id = R.string.biometric_learn_more),
                    style = MaterialTheme.typography.titleSmall,
                    textDecoration = TextDecoration.Underline,
                    fontWeight = FontWeight.Normal,
                    textAlign = TextAlign.Center
                )
            }

            HGButton(
                onClick = onUseBiometricsClick,
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
            top.linkTo(txtTitleId.bottom, 16.dp)
            end.linkTo(endGuideline)
            width = Dimension.fillToConstraints
        }

        createVerticalChain(imgBiometricId, btnLearnMoreId, chainStyle = ChainStyle.Packed)

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
            top.linkTo(btnLearnMoreId.bottom)
            end.linkTo(endGuideline)
            bottom.linkTo(parent.bottom)
            height = Dimension.preferredWrapContent
            width = Dimension.fillToConstraints
        }
    }
}

private fun mediumConstraint(): ConstraintSet {
    return ConstraintSet {
        val txtTitleId = createRefFor(TXT_TITLE_ID)
        val imgBiometricId = createRefFor(IMG_BIOMETRIC_ID)
        val txtDescriptionId = createRefFor(TXT_DESCRIPTION_ID)
        val btnLearnMoreId = createRefFor(BTN_LEARN_MORE_ID)
        val btnUseBiometricId = createRefFor(BTN_USE_BIOMETRIC_ID)

        val startGuideLine = createGuidelineFromStart(0.1f)
        val centerGuideline = createGuidelineFromStart(0.4f)
        val endGuideline = createGuidelineFromEnd(0.1f)
        val bottomGuideline = createGuidelineFromBottom(0.50f)

        constrain(imgBiometricId) {
            top.linkTo(txtTitleId.top)
            end.linkTo(centerGuideline, 16.dp)
        }
        constrain(txtTitleId) {
            start.linkTo(centerGuideline, 16.dp)
            bottom.linkTo(txtDescriptionId.top, 16.dp)
            end.linkTo(endGuideline)
            width = Dimension.fillToConstraints
        }
        constrain(txtDescriptionId) {
            start.linkTo(txtTitleId.start)
            end.linkTo(endGuideline)
            bottom.linkTo(btnLearnMoreId.top)
            width = Dimension.fillToConstraints
        }
        constrain(btnLearnMoreId) {
            start.linkTo(txtDescriptionId.start, (-12).dp)
            bottom.linkTo(bottomGuideline)
        }
        constrain(btnUseBiometricId) {
            start.linkTo(parent.start)
            top.linkTo(bottomGuideline, 64.dp)
            end.linkTo(parent.end)
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
        val centerGuideline = createGuidelineFromStart(0.4f)
        val endGuideline = createGuidelineFromEnd(0.2f)
        val bottomGuideline = createGuidelineFromBottom(0.50f)

        constrain(imgBiometricId) {
            top.linkTo(txtTitleId.top)
            end.linkTo(centerGuideline, 16.dp)
        }
        constrain(txtTitleId) {
            start.linkTo(centerGuideline, 16.dp)
            bottom.linkTo(txtDescriptionId.top, 16.dp)
            end.linkTo(endGuideline)
            width = Dimension.fillToConstraints
        }
        constrain(txtDescriptionId) {
            start.linkTo(txtTitleId.start)
            bottom.linkTo(btnLearnMoreId.top)
            end.linkTo(endGuideline)
            width = Dimension.fillToConstraints
        }
        constrain(btnLearnMoreId) {
            start.linkTo(txtDescriptionId.start, (-12).dp)
            bottom.linkTo(bottomGuideline)
        }
        constrain(btnUseBiometricId) {
            start.linkTo(parent.start)
            top.linkTo(bottomGuideline, 96.dp)
            end.linkTo(parent.end)
        }
    }
}

@MultiDevicePreview
@Composable
private fun BiometricsAuthenticationScreenPreview() {

    HealthGatewayTheme {
        BiometricsAuthenticationScreenContent(onLearnMoreClick = {}, onUseBiometricsClick = {})
    }
}

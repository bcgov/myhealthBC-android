package ca.bc.gov.bchealth.ui.tos

import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.constraintlayout.compose.ChainStyle
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.ConstraintSet
import androidx.constraintlayout.compose.Dimension
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ca.bc.gov.bchealth.R
import ca.bc.gov.bchealth.compose.BasePreview
import ca.bc.gov.bchealth.compose.component.HGButton
import ca.bc.gov.bchealth.compose.component.HGCircularProgressIndicator
import ca.bc.gov.bchealth.compose.component.HGOutlineButton
import ca.bc.gov.bchealth.compose.theme.HealthGatewayTheme

/**
 * @author pinakin.kansara
 * Created 2023-09-22 at 11:59 a.m.
 */

private const val WEB_VIEW_ID = "web_view_id"
private const val CANCEL_BTN_ID = "cancel_btn_id"
private const val AGREE_BTN_ID = "agree_btn_id"
private const val SPACE_ID = "space_id"
@Composable
fun TermsOfServiceScreen(
    onTermsOfServiceStatusChanged: (TermsOfServiceStatus) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: TermsOfServiceViewModel
) {

    val uiState by viewModel.tosUiState.collectAsStateWithLifecycle()

    LaunchedEffect(key1 = Unit) {
        viewModel.getTermsOfServices()
    }

    TermsOfServiceScreenContent(
        onTermsOfServiceStatusChanged = onTermsOfServiceStatusChanged,
        modifier,
        uiState = uiState
    )
}

@Composable
private fun TermsOfServiceScreenContent(
    onTermsOfServiceStatusChanged: (TermsOfServiceStatus) -> Unit,
    modifier: Modifier = Modifier,
    uiState: TermsOfServiceUiModel
) {

    if (uiState.showLoading) {
        HGCircularProgressIndicator()
    } else {

        BoxWithConstraints(
            modifier = modifier
                .padding(32.dp)
                .fillMaxSize()
        ) {

            ConstraintLayout(
                modifier = Modifier.fillMaxSize(),
                constraintSet = getTermsOfServiceConstraints()
            ) {
                AndroidView(modifier = Modifier.layoutId(WEB_VIEW_ID), factory = {
                    WebView(it).apply {
                        webViewClient = WebViewClient()
                        settings.textZoom = settings.textZoom + 10
                        if (!uiState.tos.isNullOrBlank()) {
                            loadDataWithBaseURL(
                                "app:htmlPage",
                                uiState.tos, "text/html", "utf-8", null
                            )
                        }
                    }
                })

                HGOutlineButton(
                    onClick = { onTermsOfServiceStatusChanged(TermsOfServiceStatus.DECLINED) },
                    modifier = Modifier.layoutId(CANCEL_BTN_ID),
                    text = stringResource(id = R.string.cancel)
                )
                Spacer(modifier = Modifier.layoutId(SPACE_ID))
                HGButton(
                    onClick = { onTermsOfServiceStatusChanged(TermsOfServiceStatus.ACCEPTED) },
                    modifier = Modifier.layoutId(AGREE_BTN_ID),
                    text = stringResource(id = R.string.btn_agree)
                )
            }
        }
    }
}

private fun getTermsOfServiceConstraints(): ConstraintSet {
    return ConstraintSet {
        val webView = createRefFor(WEB_VIEW_ID)
        val btnCancel = createRefFor(CANCEL_BTN_ID)
        val btnAgree = createRefFor((AGREE_BTN_ID))
        val space = createRefFor(SPACE_ID)

        val chain = createHorizontalChain(btnCancel, space, btnAgree, chainStyle = ChainStyle.SpreadInside)

        constrain(btnCancel) {
            start.linkTo(parent.start)
            bottom.linkTo(parent.bottom)
            end.linkTo(btnAgree.start)
            width = Dimension.fillToConstraints
        }
        constrain(btnAgree) {
            start.linkTo(space.end)
            end.linkTo(parent.end)
            bottom.linkTo(parent.bottom)
            width = Dimension.fillToConstraints
        }

        constrain(webView) {
            start.linkTo(parent.start)
            top.linkTo(parent.top)
            end.linkTo(parent.end)
            bottom.linkTo(btnCancel.top, 32.dp)
            height = Dimension.fillToConstraints
        }

        constrain(space) {
            start.linkTo(btnCancel.end)
            top.linkTo(btnCancel.top)
            bottom.linkTo(btnCancel.bottom)
            width = Dimension.value(16.dp)
            height = Dimension.fillToConstraints
        }

        constrain(chain) {
            start.linkTo(parent.start)
            end.linkTo(parent.end)
        }
    }
}

@BasePreview
@Composable
private fun TermsOfServiceScreenPreview() {
    HealthGatewayTheme {
    }
}

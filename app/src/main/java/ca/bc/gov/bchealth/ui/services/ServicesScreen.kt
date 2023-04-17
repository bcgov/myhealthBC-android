package ca.bc.gov.bchealth.ui.services

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Card
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import ca.bc.gov.bchealth.R
import ca.bc.gov.bchealth.compose.BasePreview
import ca.bc.gov.bchealth.compose.MyHealthTheme
import ca.bc.gov.bchealth.compose.MyHealthTypography
import ca.bc.gov.common.model.services.OrganDonorStatusDto

@Composable
fun ServicesScreen(
    modifier: Modifier,
    viewModel: ServicesViewModel,
    onRegisterOnUpdateDecisionClicked: (String) -> Unit,
    onDownloadButtonClicked: (String) -> Unit
) {
    val uiState = viewModel.uiState.collectAsState()

    ServiceScreenContent(
        modifier = modifier,
        onLoading = uiState.value.onLoading,
        organDonationRegistrationDetail = uiState.value.organDonationRegistrationDetail,
        onRegisterOnUpdateDecisionClicked = { url ->
            onRegisterOnUpdateDecisionClicked(url)
        }
    ) {
        uiState.value.organDonationRegistrationDetail?.file?.let { file ->
            onDownloadButtonClicked(
                file
            )
        }
    }
}

@Composable
private fun ServiceScreenContent(
    modifier: Modifier = Modifier,
    onLoading: Boolean,
    organDonationRegistrationDetail: OrganDonationRegistrationDetail?,
    onRegisterOnUpdateDecisionClicked: (String) -> Unit,
    onDownloadButtonClicked: () -> Unit
) {

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(start = 32.dp, end = 32.dp, bottom = 32.dp)
    ) {

        Text(
            text = stringResource(id = R.string.services),
            style = MyHealthTypography.h2,
            color = MaterialTheme.colors.primary
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            modifier = Modifier.fillMaxWidth(),
            text = stringResource(id = R.string.services_subtitle),
            style = MyHealthTypography.h4
        )
        Spacer(modifier = Modifier.height(16.dp))

        if (onLoading) {
            Box(
                modifier = modifier
                    .fillMaxSize()
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                )
            }
        } else {
            organDonationRegistrationDetail?.let {
                OrganDonor(it, onRegisterOnUpdateDecisionClicked = { url ->
                    onRegisterOnUpdateDecisionClicked(url)
                }) {
                    onDownloadButtonClicked()
                }
            }
        }
    }
}

@Composable
private fun OrganDonor(
    organDonationRegistrationDetail: OrganDonationRegistrationDetail,
    onRegisterOnUpdateDecisionClicked: (String) -> Unit,
    onDownloadButtonClicked: () -> Unit
) {

    Card(
        modifier = Modifier
            .fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        elevation = 4.dp,
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            Row(verticalAlignment = Alignment.Bottom) {
                Image(
                    painter = painterResource(id = R.drawable.ic_organ_donor),
                    contentDescription = stringResource(id = R.string.organ_donor),
                )
                Spacer(
                    modifier = Modifier
                        .width(8.dp)
                )
                Text(
                    text = stringResource(id = R.string.organ_donor_registration_title),
                    style = MaterialTheme.typography.h4,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row() {
                Text(
                    text = stringResource(id = R.string.organ_donor_status),
                    style = MaterialTheme.typography.h4
                )
                Spacer(
                    modifier = Modifier
                        .width(16.dp)
                )
                Text(
                    text = organDonationRegistrationDetail.status.value,
                    style = MaterialTheme.typography.h4,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.height(8.dp))

            val statusMessage = organDonationRegistrationDetail.statusMessage
            Text(
                text = statusMessage
                    ?: stringResource(id = R.string.organ_donor_not_registered_subtitle),
                style = MaterialTheme.typography.body1
            )

            Spacer(modifier = Modifier.height(32.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = stringResource(id = R.string.organ_donor_decision),
                    style = MaterialTheme.typography.h4
                )
                Spacer(
                    modifier = Modifier
                        .width(16.dp)
                )

                when (organDonationRegistrationDetail.status) {
                    OrganDonorStatusDto.REGISTERED -> {
                        OutlinedButton(
                            onClick = { onDownloadButtonClicked() },
                            modifier = Modifier.fillMaxWidth(),
                            border = BorderStroke(1.dp, color = MaterialTheme.colors.primary)
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_download_pdf),
                                contentDescription = stringResource(id = R.string.download)
                            )
                            Spacer(modifier = Modifier.size(ButtonDefaults.IconSize))
                            Text(
                                text = stringResource(id = R.string.organ_donor_decision_download),
                                style = MaterialTheme.typography.body1,
                                color = MaterialTheme.colors.primary
                            )
                        }
                    }

                    else -> {
                        Text(
                            text = stringResource(id = R.string.organ_donor_decision_not_available),
                            style = MaterialTheme.typography.h4,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            RegisterOrUpdateDecision() {
                onRegisterOnUpdateDecisionClicked(it)
            }
        }
    }
}

@Composable
private fun RegisterOrUpdateDecision(onRegisterOnUpdateDecisionClicked: (String) -> Unit) {
    val link = stringResource(id = R.string.organ_donor_registration_link)
    val annotatedString = buildAnnotatedString {
        withStyle(
            style = SpanStyle(
                color = MaterialTheme.colors.primary,
                fontWeight = FontWeight.Bold,
                textDecoration = TextDecoration.Underline
            )
        ) {
            append(text = stringResource(id = R.string.organ_donor_register_or_update))
        }
    }
    ClickableText(
        text = annotatedString,
        style = MaterialTheme.typography.body1,
        onClick = { offset ->
            onRegisterOnUpdateDecisionClicked(link)
        }
    )
}

@Composable
@BasePreview
private fun ServiceScreenContentPreview() {
    MyHealthTheme {
        ServiceScreenContent(
            onLoading = true,
            organDonationRegistrationDetail = OrganDonationRegistrationDetail(
                status = OrganDonorStatusDto.REGISTERED
            ),
            onRegisterOnUpdateDecisionClicked = {},
            onDownloadButtonClicked = {}
        )
    }
}

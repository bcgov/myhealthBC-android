package ca.bc.gov.bchealth.ui.services

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
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
import ca.bc.gov.bchealth.compose.component.HGCircularProgressIndicator
import ca.bc.gov.common.model.services.OrganDonorStatusDto

@Composable
fun ServicesScreen(
    modifier: Modifier,
    viewModel: ServicesViewModel,
    onRegisterOnUpdateDecisionClicked: (String) -> Unit,
    onDownloadButtonClicked: (String) -> Unit,
    openPdfFile: (String?) -> Unit,
    onError: () -> Unit
) {
    val uiState = viewModel.uiState.collectAsState()

    ServiceScreenContent(
        modifier = modifier,
        onLoading = uiState.value.onLoading,
        organDonorRegistrationDetail = uiState.value.organDonorRegistrationDetail,
        organDonorFileStatus = uiState.value.organDonorFileStatus,
        onRegisterOnUpdateDecisionClicked = { url ->
            onRegisterOnUpdateDecisionClicked(url)
        },
        onDownloadButtonClicked = {
            uiState.value.organDonorRegistrationDetail?.fileId?.let { fileId ->
                onDownloadButtonClicked(
                    fileId
                )
            }
        },
        openPdfFile = {
            openPdfFile(uiState.value.organDonorRegistrationDetail?.file)
        },
        onError = onError
    )
    if (uiState.value.organDonorFileStatus == OrganDonorFileStatus.DOWNLOADED) {
        openPdfFile(uiState.value.organDonorRegistrationDetail?.file)
        viewModel.onPdfViewed()
    }
}

@Composable
private fun ServiceScreenContent(
    modifier: Modifier = Modifier,
    onLoading: Boolean,
    organDonorRegistrationDetail: OrganDonorRegistrationDetail?,
    organDonorFileStatus: OrganDonorFileStatus,
    onRegisterOnUpdateDecisionClicked: (String) -> Unit,
    onDownloadButtonClicked: () -> Unit,
    openPdfFile: (String?) -> Unit,
    onError: () -> Unit
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

        if (onLoading || organDonorRegistrationDetail == null) {
            HGCircularProgressIndicator(modifier)
        } else {
            OrganDonor(organDonorRegistrationDetail, organDonorFileStatus, onRegisterOnUpdateDecisionClicked = { url ->
                onRegisterOnUpdateDecisionClicked(url)
            }, onDownloadButtonClicked = { onDownloadButtonClicked() }, openPdfFile = { file ->
                file?.let { pdf -> openPdfFile(pdf) }
            }, onError = onError)
        }
    }
}

@Composable
private fun OrganDonor(
    organDonorRegistrationDetail: OrganDonorRegistrationDetail,
    organDonorFileStatus: OrganDonorFileStatus,
    onRegisterOnUpdateDecisionClicked: (String) -> Unit,
    onDownloadButtonClicked: () -> Unit,
    openPdfFile: (String?) -> Unit,
    onError: () -> Unit
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
                    text = organDonorRegistrationDetail.status.value,
                    style = MaterialTheme.typography.h4,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.height(8.dp))

            val statusMessage = organDonorRegistrationDetail.statusMessage
            Text(
                text = statusMessage
                    ?: "",
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

                when (organDonorRegistrationDetail.status) {
                    OrganDonorStatusDto.REGISTERED -> {
                        OutlinedButton(
                            onClick = {
                                if (organDonorFileStatus == OrganDonorFileStatus.DOWNLOADED) {
                                    openPdfFile(organDonorRegistrationDetail.file)
                                } else {
                                    onDownloadButtonClicked()
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            border = BorderStroke(1.dp, color = MaterialTheme.colors.primary)
                        ) {

                            var buttonText = stringResource(id = R.string.organ_donor_decision_download)
                            when (organDonorFileStatus) {
                                OrganDonorFileStatus.ERROR -> {
                                    onError()
                                    buttonText = stringResource(id = R.string.retry)
                                    Icon(
                                        painter = painterResource(id = R.drawable.ic_download_pdf),
                                        contentDescription = stringResource(id = R.string.download)
                                    )
                                }
                                OrganDonorFileStatus.REQUIRE_DOWNLOAD,
                                OrganDonorFileStatus.DOWNLOADED -> {
                                    Icon(
                                        painter = painterResource(id = R.drawable.ic_download_pdf),
                                        contentDescription = stringResource(id = R.string.download)
                                    )
                                }
                                OrganDonorFileStatus.DOWNLOAD_IN_PROGRESS -> {
                                    buttonText = stringResource(id = R.string.downloading)
                                    CircularProgressIndicator(modifier = Modifier.size(24.dp), color = MaterialTheme.colors.primary)
                                }
                            }

                            Spacer(modifier = Modifier.size(ButtonDefaults.IconSize))
                            Text(
                                text = buttonText,
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
            onLoading = false,
            organDonorRegistrationDetail = OrganDonorRegistrationDetail(
                status = OrganDonorStatusDto.REGISTERED
            ),
            organDonorFileStatus = OrganDonorFileStatus.DOWNLOAD_IN_PROGRESS,
            onRegisterOnUpdateDecisionClicked = {},
            onDownloadButtonClicked = {},
            openPdfFile = {},
            onError = {}
        )
    }
}

package ca.bc.gov.bchealth.ui.profile

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import ca.bc.gov.bchealth.R
import ca.bc.gov.bchealth.compose.BasePreview
import ca.bc.gov.bchealth.compose.MyHealthTypography
import ca.bc.gov.bchealth.compose.darkText
import ca.bc.gov.bchealth.compose.primaryBlue
import ca.bc.gov.bchealth.ui.custom.DecorativeImage
import ca.bc.gov.bchealth.ui.custom.MyHealthClickableText
import ca.bc.gov.bchealth.ui.custom.MyHealthScaffold
import ca.bc.gov.bchealth.ui.dependents.profile.DependentProfileItem
import ca.bc.gov.bchealth.ui.dependents.profile.ListDivider
import ca.bc.gov.bchealth.utils.URL_ADDRESS_CHANGE

@Composable
fun ProfileUI(
    viewModel: ProfileViewModel,
    navigationAction: () -> Unit,
    onClickAddress: () -> Unit,
    onClickPrefs: () -> Unit,
) {
    val uiState = viewModel.uiState.collectAsState().value

    MyHealthScaffold(
        title = stringResource(id = uiState.title),
        isLoading = uiState.loading,
        navigationAction = navigationAction
    ) {
        ProfileContent(uiState, onClickAddress, onClickPrefs)
    }
}

@Composable
private fun BoxScope.ProfileContent(
    uiState: ProfileUiState,
    onClickAddress: () -> Unit,
    onClickPrefs: () -> Unit,
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .align(Alignment.TopCenter)
    ) {

        item { ProfileHeaderUi(uiState.fullName) }

        item { ListDivider() }

        uiState.uiList.forEach { listItem ->
            item {
                when (listItem) {
                    is ProfileItem.Info -> DependentProfileItem(
                        stringResource(id = listItem.label),
                        listItem.content
                    )

                    is ProfileItem.Address -> ProfileAddressUi(
                        stringResource(id = listItem.label),
                        listItem.content,
                        stringResource(id = listItem.footer),
                        stringResource(id = listItem.clickableText),
                        onClickAddress
                    )

                    is ProfileItem.EmptyAddress -> ProfileEmptyAddressUi(
                        stringResource(id = listItem.label),
                        stringResource(id = listItem.placeholder),
                        stringResource(id = listItem.footer),
                        stringResource(id = listItem.clickableText),
                        onClickAddress
                    )
                }
            }

            item { ListDivider() }
        }

        item {
            CommunicationPreferences(
                email = uiState.email,
                verified = uiState.isEmailVerified,
                phone = uiState.phone,
                onClick = onClickPrefs,
            )
        }
    }
}

@Composable
fun ProfileHeaderUi(fullName: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(top = 16.dp, bottom = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        DecorativeImage(resourceId = R.drawable.ic_profile_image)

        Text(
            text = fullName,
            style = MyHealthTypography.h3,
            color = primaryBlue,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .padding(top = 16.dp)
                .fillMaxWidth()
        )
    }
}

@Composable
fun ProfileAddressUi(
    label: String,
    value: String,
    footer: String,
    clickableText: String,
    onClickAction: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(top = 20.dp, bottom = 20.dp, start = 32.dp),
    ) {
        Text(text = label, style = MyHealthTypography.body1)
        Text(
            text = value,
            style = MyHealthTypography.body2,
            modifier = Modifier.padding(top = 4.dp)
        )
        MyHealthClickableText(
            modifier = Modifier.padding(top = 4.dp),
            style = MyHealthTypography.caption,
            fullText = footer,
            clickableText = clickableText,
            action = onClickAction,
            clickableStyle = SpanStyle(
                color = primaryBlue,
                textDecoration = TextDecoration.Underline,
                fontWeight = FontWeight.Bold
            )
        )
    }
}

@Composable
fun ProfileEmptyAddressUi(
    label: String,
    value: String,
    footer: String,
    clickableText: String,
    onClickAction: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(top = 20.dp, bottom = 20.dp, start = 32.dp),
    ) {
        Text(text = label, style = MyHealthTypography.body1)
        Text(
            text = value,
            style = MyHealthTypography.caption,
            modifier = Modifier.padding(top = 4.dp)
        )
        MyHealthClickableText(
            modifier = Modifier.padding(top = 4.dp),
            style = MyHealthTypography.caption.copy(color = darkText),
            fullText = footer,
            clickableText = clickableText,
            action = onClickAction
        )
    }
}

@BasePreview
@Composable
private fun PreviewClinicalDocumentDetailContent() {
    Box {
        ProfileContent(
            ProfileUiState(
                fullName = "Jean Smith",
                uiList = listOf(
                    ProfileItem.Info(
                        R.string.profile_first_name,
                        "Jean"
                    ),
                    ProfileItem.Info(
                        R.string.profile_last_name,
                        "Smith"
                    ),
                    ProfileItem.Info(
                        R.string.profile_phn,
                        "4444 555 999"
                    ),
                    ProfileItem.Address(
                        R.string.profile_physical_address,
                        "Vancouver, BC V8V 2T2",
                        R.string.profile_address_footer,
                        R.string.profile_address_footer_click,
                        URL_ADDRESS_CHANGE
                    ),

                    ProfileItem.EmptyAddress(
                        R.string.profile_physical_address,
                        R.string.profile_address_empty,
                        R.string.profile_address_empty_footer,
                        R.string.profile_address_empty_footer_click,
                        URL_ADDRESS_CHANGE
                    ),
                ),
            ),
            onClickAddress = {},
            onClickPrefs = {}
        )
    }
}

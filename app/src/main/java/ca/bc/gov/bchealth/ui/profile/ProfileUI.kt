package ca.bc.gov.bchealth.ui.profile

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ca.bc.gov.bchealth.R
import ca.bc.gov.bchealth.compose.MyHealthTypography
import ca.bc.gov.bchealth.compose.darkText
import ca.bc.gov.bchealth.ui.custom.MyHealthScaffold
import ca.bc.gov.bchealth.ui.dependents.profile.DependentProfileItem
import ca.bc.gov.bchealth.ui.dependents.profile.ListDivider
import ca.bc.gov.bchealth.utils.URL_ADDRESS_CHANGE

@Composable
fun ProfileUI(
    viewModel: ProfileViewModel,
    navigationAction: () -> Unit
) {
    val uiState = viewModel.uiState.collectAsState().value

    MyHealthScaffold(
        title = stringResource(id = uiState.title),
        navigationAction = navigationAction
    ) {
        ProfileContent(uiState)
    }
}

@Composable
private fun ProfileContent(
    uiState: ProfileUiState,
    //  onClickDownload: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize(),
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .align(Alignment.TopCenter)
        ) {

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
                        )
                        is ProfileItem.EmptyAddress -> ProfileEmptyAddressUi(
                            stringResource(id = listItem.label),
                            stringResource(id = listItem.placeholder),
                            stringResource(id = listItem.footer),
                        )
                    }
                }

                item { ListDivider() }
            }
        }
        if (uiState.loading) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center),
            )
        }
    }
}

@Composable
fun ProfileAddressUi(label: String, value: String, footer: String) {
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
        Text(
            text = footer,
            style = MyHealthTypography.caption,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}

@Composable
fun ProfileEmptyAddressUi(label: String, value: String, footer: String) {
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
        Text(
            text = footer,
            style = MyHealthTypography.caption,
            color = darkText,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
private fun PreviewClinicalDocumentDetailContent() {
    ProfileContent(
        ProfileUiState(
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
            )
        )
    )
}

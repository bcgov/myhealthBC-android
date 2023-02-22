package ca.bc.gov.bchealth.ui.dependents.profile

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ca.bc.gov.bchealth.R
import ca.bc.gov.bchealth.compose.MyHealthTypography
import ca.bc.gov.bchealth.compose.primaryBlue
import ca.bc.gov.bchealth.compose.statusBlue30
import ca.bc.gov.bchealth.compose.white
import ca.bc.gov.bchealth.ui.custom.DecorativeImage
import ca.bc.gov.bchealth.ui.custom.MyHealthScaffold
import ca.bc.gov.common.model.dependents.DependentDto
import java.time.Instant

@Composable
fun DependentProfileUI(
    viewModel: DependentProfileViewModel,
    navigationAction: () -> Unit,
    onClickRemove: (DependentDto?) -> Unit
) {
    val uiState = viewModel.uiState.collectAsState().value

    MyHealthScaffold(
        title = stringResource(id = R.string.dependents_profile),
        isLoading = uiState.isLoading,
        navigationAction = navigationAction
    ) {
        DependentProfileContent(uiState, onClickRemove)
    }
}

@Composable
fun DependentProfileContent(
    uiState: DependentProfileViewModel.DependentProfileUiState,
    onClickRemove: (DependentDto?) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        ProfileHeaderUi(uiState.dto?.getFullName().orEmpty())

        ListDivider()

        uiState.dependentInfo.forEach {

            DependentProfileItem(stringResource(id = it.label), it.value)

            ListDivider()
        }

        Spacer(modifier = Modifier.weight(1f))

        Button(
            modifier = Modifier
                .fillMaxWidth()
                .defaultMinSize(minHeight = 56.dp)
                .padding(top = 20.dp, bottom = 20.dp, start = 32.dp, end = 32.dp),
            onClick = { onClickRemove(uiState.dto) },
            enabled = uiState.dto != null
        ) {
            Text(
                color = white,
                text = stringResource(id = R.string.dependents_profile_remove_dependent)
            )
        }
    }
}

@Composable
private fun ProfileHeaderUi(fullName: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(top = 16.dp, bottom = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        DecorativeImage(
            modifier = Modifier.size(32.dp),
            resourceId = R.drawable.ic_manage_dependent
        )

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
fun DependentProfileItem(label: String, value: String) {
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
    }
}

@Composable
fun ListDivider() {
    Divider(
        thickness = 0.5.dp,
        color = statusBlue30
    )
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
private fun PreviewDependentProfileContent() {

    val uiState = DependentProfileViewModel.DependentProfileUiState(
        dto = DependentDto(
            "hdid",
            "firstname",
            "lastname",
            "phn",
            Instant.now(),
            "gender",
            "owner",
            "delegate",
            1,
            1,
            -1,
            true
        ),
        dependentInfo = listOf(
            DependentProfileViewModel.DependentProfileItem(R.string.name, "name1"),
            DependentProfileViewModel.DependentProfileItem(R.string.name, "name2"),
            DependentProfileViewModel.DependentProfileItem(R.string.name, "name3"),
            DependentProfileViewModel.DependentProfileItem(R.string.name, "name4"),
        )
    )

    MyHealthScaffold(
        title = stringResource(id = R.string.dependents_profile),
        isLoading = uiState.isLoading,
        navigationAction = {}
    ) {
        DependentProfileContent(uiState) {}
    }
}

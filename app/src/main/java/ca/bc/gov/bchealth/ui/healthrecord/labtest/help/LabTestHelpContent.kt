package ca.bc.gov.bchealth.ui.healthrecord.labtest.help

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import ca.bc.gov.bchealth.R
import ca.bc.gov.bchealth.compose.BasePreview
import ca.bc.gov.bchealth.compose.MyHealthTypography
import ca.bc.gov.bchealth.compose.bold
import ca.bc.gov.bchealth.compose.theme.primaryBlue
import ca.bc.gov.bchealth.utils.URL_SMART_SEX_RESOURCE
import ca.bc.gov.bchealth.utils.URL_HEALTHLINK
import ca.bc.gov.bchealth.utils.URL_MAYO_CLINIC
import ca.bc.gov.bchealth.utils.URL_PATHOLOGY_TESTS

@Composable
fun LabTestHelpContent(onClickLink: (String) -> Unit) {
    val linksList = listOf(
        stringResource(R.string.lab_test_help_link_healthlink) to URL_HEALTHLINK,
        stringResource(R.string.lab_test_help_link_mayo_clinic) to URL_MAYO_CLINIC,
        stringResource(R.string.lab_test_help_link_pathology) to URL_PATHOLOGY_TESTS,
        stringResource(R.string.lab_test_help_link_smart_sex_resource) to URL_SMART_SEX_RESOURCE,
    )
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            modifier = Modifier.padding(top = 16.dp),
            text = stringResource(id = R.string.lab_test_help_body),
            style = MyHealthTypography.body2
        )

        Text(
            modifier = Modifier.padding(top = 4.dp),
            style = MyHealthTypography.body2,
            text = buildAnnotatedString {
                withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                    append(stringResource(id = R.string.lab_test_help_body_item_1_bold))
                }
                append(" " + stringResource(id = R.string.lab_test_help_body_item_1))
            }
        )

        Text(
            modifier = Modifier.padding(top = 2.dp),
            style = MyHealthTypography.body2,
            text = buildAnnotatedString {
                withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                    append(stringResource(id = R.string.lab_test_help_body_item_2_bold))
                }
                append(" " + stringResource(id = R.string.lab_test_help_body_item_2))
            }
        )

        Spacer(modifier = Modifier.size(16.dp))
        Divider()
        Spacer(modifier = Modifier.size(16.dp))

        Text(
            text = stringResource(R.string.lab_test_help_related_info),
            style = MyHealthTypography.body2.bold()
        )

        linksList.forEach { pair ->
            Text(
                modifier = Modifier
                    .padding(vertical = 8.dp)
                    .clickable {
                        onClickLink(pair.second)
                    },
                text = pair.first,
                style = MyHealthTypography.body2.copy(
                    color = primaryBlue,
                    textDecoration = TextDecoration.Underline,
                    fontWeight = FontWeight.Bold
                )
            )
        }
    }
}

@BasePreview
@Composable
fun LabTestHelpContentPreview() {
    LabTestHelpContent({})
}

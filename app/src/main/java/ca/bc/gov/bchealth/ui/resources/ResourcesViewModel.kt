package ca.bc.gov.bchealth.ui.resources

import androidx.lifecycle.ViewModel
import ca.bc.gov.bchealth.R

class ResourcesViewModel : ViewModel() {

    fun getResourcesList(): List<ResourceItem> = listOf(
        ResourceItem(
            R.drawable.ic_resources_update_immnz,
            R.string.label_resource_update_your_immnz,
            URL_UPDATE_YOUR_IMMNZ
        ),
        ResourceItem(
            R.drawable.ic_resources_advice,
            R.string.label_resource_advice,
            URL_GET_HEALTH_ADVICE
        ),
        ResourceItem(
            R.drawable.ic_resources_how_to_get_vax,
            R.string.label_resource_how_to_get_vax,
            URL_HOW_TO_GET_COVID_VACCINATED
        ),
        ResourceItem(
            R.drawable.ic_resources_symptom,
            R.string.label_resource_symptom,
            URL_COVID_SYMPTOM_CHECKER
        ),
        ResourceItem(
            R.drawable.ic_resources_get_tested,
            R.string.label_resource_get_tested,
            URL_GET_TESTED_FOR_COVID
        ),
    )
}

private const val URL_UPDATE_YOUR_IMMNZ =
    "https://immunizationrecord.gov.bc.ca/"

private const val URL_GET_HEALTH_ADVICE =
    "https://www.healthlinkbc.ca/"

private const val URL_HOW_TO_GET_COVID_VACCINATED =
    "https://www2.gov.bc.ca/gov/content/covid-19/vaccine/register"

private const val URL_GET_TESTED_FOR_COVID =
    "http://www.bccdc.ca/health-info/diseases-conditions/covid-19/testing/where-to-get-a-covid-19-test-in-bc"

private const val URL_COVID_SYMPTOM_CHECKER =
    "https://bc.thrive.health/covid19/en"

data class ResourceItem(
    val icon: Int,
    val title: Int,
    val link: String,
)

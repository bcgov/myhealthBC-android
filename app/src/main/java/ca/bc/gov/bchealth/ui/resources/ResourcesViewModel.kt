package ca.bc.gov.bchealth.ui.resources

import androidx.lifecycle.ViewModel
import ca.bc.gov.bchealth.R

class ResourcesViewModel : ViewModel() {

    fun getResourcesList(): List<ResourceItem> = listOf(
        ResourceItem(
            R.drawable.ic_resources_update_immnz,
            R.string.label_resource_update_your_immnz,
            R.string.url_update_your_immnz
        ),
        ResourceItem(
            R.drawable.ic_resources_advice,
            R.string.label_resource_advice,
            R.string.url_get_health_advice
        ),
        ResourceItem(
            R.drawable.ic_resources_how_to_get_vax,
            R.string.label_resource_how_to_get_vax,
            R.string.url_how_to_get_covid_vaccinated
        ),
        ResourceItem(
            R.drawable.ic_resources_symptom,
            R.string.label_resource_symptom,
            R.string.url_covid_symptom_checker
        ),
        ResourceItem(
            R.drawable.ic_resources_get_tested,
            R.string.label_resource_get_tested,
            R.string.url_get_tested_for_covid
        ),
    )
}

data class ResourceItem(
    val icon: Int,
    val title: Int,
    val link: Int,
)

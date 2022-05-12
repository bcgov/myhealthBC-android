package ca.bc.gov.bchealth.ui.resources

import androidx.lifecycle.ViewModel
import ca.bc.gov.bchealth.R

/*
* Created by amit_metri on 05,April,2022
*/
class ResourcesViewModel : ViewModel() {

    fun prepareResourcesList(): MutableList<Resources> {
        val resources = mutableListOf<Resources>()
        resources.add(
            Resources(
                R.drawable.ic_resources_icon_1,
                R.string.label_resource_1,
                R.string.url_get_health_advice
            )
        )
        resources.add(
            Resources(
                R.drawable.ic_resources_icon_2,
                R.string.label_resource_2,
                R.string.url_how_to_get_covid_vaccinated
            )
        )
        resources.add(
            Resources(
                R.drawable.ic_resources_icon_4,
                R.string.label_resource_4,
                R.string.url_covid_symptom_checker
            )
        )
        resources.add(
            Resources(
                R.drawable.ic_resources_icon_3,
                R.string.label_resource_3,
                R.string.url_get_tested_for_covid
            )
        )
        return resources
    }
}

data class Resources(
    val icon: Int,
    val title: Int,
    val link: Int,
)

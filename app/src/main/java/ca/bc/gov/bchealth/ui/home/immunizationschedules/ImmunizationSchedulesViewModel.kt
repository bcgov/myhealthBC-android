package ca.bc.gov.bchealth.ui.home.immunizationschedules

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import ca.bc.gov.bchealth.R

class ImmunizationSchedulesViewModel : ViewModel() {

    fun getUiList(): List<ImmunizationSchedulesItem> = listOf(
        ImmunizationSchedulesItem(
            R.drawable.ic_immnz_schedules_infant,
            R.string.immnz_schedules_infant,
            R.string.url_immnz_schedules_infant,
        ),
        ImmunizationSchedulesItem(
            R.drawable.ic_immnz_schedules_school_age,
            R.string.immnz_schedules_school_age,
            R.string.url_immnz_schedules_school_age,
        ),
        ImmunizationSchedulesItem(
            R.drawable.ic_immnz_schedules_adult_seniors,
            R.string.immnz_schedules_adult_seniors,
            R.string.url_immnz_schedules_adult_seniors,
        )
    )

    data class ImmunizationSchedulesItem(
        @DrawableRes val icon: Int,
        @StringRes val title: Int,
        @StringRes val url: Int,
    )
}

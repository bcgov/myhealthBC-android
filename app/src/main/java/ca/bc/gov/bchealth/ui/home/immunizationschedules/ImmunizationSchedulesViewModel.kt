package ca.bc.gov.bchealth.ui.home.immunizationschedules

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ca.bc.gov.bchealth.R
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ImmunizationSchedulesViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(ImmunizationSchedulesUiState())
    val uiState: StateFlow<ImmunizationSchedulesUiState> = _uiState.asStateFlow()

    fun loadUiList() = viewModelScope.launch {
        _uiState.update {
            ImmunizationSchedulesUiState(
                uiList = listOf(
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
            )
        }
    }

    data class ImmunizationSchedulesUiState(
        val uiList: List<ImmunizationSchedulesItem> = listOf()
    )

    data class ImmunizationSchedulesItem(
        @DrawableRes val icon: Int,
        @StringRes val title: Int,
        @StringRes val url: Int,
    )
}

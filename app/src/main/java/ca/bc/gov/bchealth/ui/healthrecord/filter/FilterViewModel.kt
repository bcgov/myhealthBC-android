package ca.bc.gov.bchealth.ui.healthrecord.filter

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class FilterViewModel @Inject constructor(): ViewModel() {
    var timelineTypeFilter: List<TimelineTypeFilter> = emptyList()
}

enum class TimelineTypeFilter {
    MEDICATION,
    LAB_TEST,
    COVID_19_TEST,
    IMMUNIZATION
}
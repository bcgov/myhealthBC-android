package ca.bc.gov.bchealth.ui.healthrecord.individual

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class FilterViewModel @Inject constructor(): ViewModel() {
    var timelineTypeFilter: List<TimelineTypeFilter> = listOf(TimelineTypeFilter.ALL)
}

enum class TimelineTypeFilter {
    ALL,
    MEDICATION,
    LAB_TEST,
    COVID_19_TEST,
    IMMUNIZATION
}
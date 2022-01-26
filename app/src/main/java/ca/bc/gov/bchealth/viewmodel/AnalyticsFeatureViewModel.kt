package ca.bc.gov.bchealth.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ca.bc.gov.common.model.analytics.AnalyticsAction
import ca.bc.gov.common.model.analytics.AnalyticsActionData
import ca.bc.gov.common.model.settings.AnalyticsFeature
import ca.bc.gov.repository.analytics.AnalyticsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * @author Pinakin Kansara
 */
@HiltViewModel
class AnalyticsFeatureViewModel @Inject constructor(
    private val repository: AnalyticsRepository
) : ViewModel() {

    val analyticsFeature = repository.analyticsFeature.shareIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        replay = 0
    )

    fun toggleAnalyticsFeature(state: AnalyticsFeature) = viewModelScope.launch {
        repository.toggleAnalyticsFeature(state)
    }

    fun track(action: AnalyticsAction, data: AnalyticsActionData) = viewModelScope.launch {
        withContext(Dispatchers.IO) {
            repository.track(action, data)
        }
    }

    fun track(action: AnalyticsAction, data: String) = viewModelScope.launch {
        withContext(Dispatchers.IO) {
            repository.track(action, data)
        }
    }
}

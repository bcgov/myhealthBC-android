package ca.bc.gov.bchealth.ui.recommendations

import androidx.lifecycle.ViewModel
import ca.bc.gov.bchealth.model.mapper.toUiModel
import ca.bc.gov.common.model.immunization.ForecastStatus
import ca.bc.gov.repository.immunization.ImmunizationRecommendationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@HiltViewModel
class RecommendationsViewModel @Inject constructor(
    recommendationRepository: ImmunizationRecommendationRepository,
) : ViewModel() {

    val recommendationList = recommendationRepository.getAllRecommendations().map { list ->
        list.mapIndexed { index, dto ->
            dto.toUiModel().apply {
                expandFirstItem(index)
            }
        }
    }

    private fun RecommendationDetailItem.expandFirstItem(index: Int) {
        if (index == 0) this.fullContent = true
    }
}

data class RecommendationDetailItem(
    val title: String,
    val status: ForecastStatus?,
    val date: String,
    var fullContent: Boolean = false,
)

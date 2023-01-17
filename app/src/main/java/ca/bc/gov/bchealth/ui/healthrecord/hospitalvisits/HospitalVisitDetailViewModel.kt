package ca.bc.gov.bchealth.ui.healthrecord.hospitalvisits

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ca.bc.gov.bchealth.R
import ca.bc.gov.common.model.hospitalvisits.HospitalVisitDto
import ca.bc.gov.common.utils.toDateTimeString
import ca.bc.gov.repository.hospitalvisit.HospitalVisitRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HospitalVisitDetailViewModel @Inject constructor(
    private val repository: HospitalVisitRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(HospitalVisitUiState())
    val uiState: StateFlow<HospitalVisitUiState> = _uiState.asStateFlow()

    fun getHospitalVisitDetails(hospitalVisitId: Long) = viewModelScope.launch {
        try {
            val dto: HospitalVisitDto = repository.getHospitalVisit(hospitalVisitId)

            val uiList: List<HospitalVisitDetailItem> = listOf(
                HospitalVisitDetailItem(
                    R.string.hospital_visits_detail_location_title,
                    dto.location,
                    R.string.hospital_visits_detail_location_footer
                ),

                HospitalVisitDetailItem(
                    R.string.hospital_visits_detail_provider_title,
                    dto.provider,
                    R.string.hospital_visits_detail_provider_footer
                ),

                HospitalVisitDetailItem(
                    R.string.hospital_visits_detail_visit_type_title,
                    dto.visitType,
                ),

                HospitalVisitDetailItem(
                    R.string.hospital_visits_detail_visit_date_title,
                    dto.visitDate.toDateTimeString()
                ),

                HospitalVisitDetailItem(
                    R.string.hospital_visits_detail_discharge_date_title,
                    dto.dischargeDate?.toDateTimeString().orEmpty(),
                ),
            )

            _uiState.update { state ->
                state.copy(
                    onLoading = false,
                    toolbarTitle = dto.healthService,
                    uiList = uiList
                )
            }
        } catch (e: Exception) {
            _uiState.update { state ->
                state.copy(onLoading = false)
            }
        }
    }
}

data class HospitalVisitUiState(
    val onLoading: Boolean = false,
    val toolbarTitle: String? = "",
    val uiList: List<HospitalVisitDetailItem> = emptyList()
)

data class HospitalVisitDetailItem(
    val title: Int,
    val description: String,
    val footer: Int? = null,
)

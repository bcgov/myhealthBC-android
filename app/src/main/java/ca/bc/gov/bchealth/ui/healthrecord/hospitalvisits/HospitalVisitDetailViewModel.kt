package ca.bc.gov.bchealth.ui.healthrecord.hospitalvisits

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ca.bc.gov.bchealth.R
import ca.bc.gov.bchealth.ui.healthrecord.HealthRecordDetailItem
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

            val dischargeDate = dto.dischargeDate?.toDateTimeString().orEmpty()

            val uiList: List<HealthRecordDetailItem> = listOf(
                HealthRecordDetailItem(
                    title = R.string.hospital_visits_detail_location_title,
                    description = dto.location,
                    footer = R.string.hospital_visits_detail_location_footer
                ),

                HealthRecordDetailItem(
                    title = R.string.hospital_visits_detail_provider_title,
                    description = dto.provider,
                    placeholder = dto.provider.getPlaceholder(),
                    footer = R.string.hospital_visits_detail_provider_footer
                ),

                HealthRecordDetailItem(
                    title = R.string.hospital_visits_detail_service_title,
                    placeholder = dto.healthService.getPlaceholder(),
                    description = dto.healthService
                ),

                HealthRecordDetailItem(
                    title = R.string.hospital_visits_detail_visit_type_title,
                    description = dto.visitType,
                ),

                HealthRecordDetailItem(
                    title = R.string.hospital_visits_detail_visit_date_title,
                    description = dto.visitDate.toDateTimeString()
                ),

                HealthRecordDetailItem(
                    title = R.string.hospital_visits_detail_discharge_date_title,
                    description = dischargeDate,
                    placeholder = dischargeDate.getPlaceholder()
                ),
            )

            _uiState.update { state ->
                state.copy(
                    onLoading = false,
                    toolbarTitle = dto.location,
                    uiList = uiList,
                    id = dto.encounterId
                )
            }
        } catch (e: Exception) {
            _uiState.update { state ->
                state.copy(onLoading = false)
            }
        }
    }

    private fun String.getPlaceholder(): Int? =
        if (this.isBlank()) R.string.not_available else null
}

data class HospitalVisitUiState(
    val onLoading: Boolean = false,
    val toolbarTitle: String? = "",
    val id: String? = "",
    val uiList: List<HealthRecordDetailItem> = emptyList()
)

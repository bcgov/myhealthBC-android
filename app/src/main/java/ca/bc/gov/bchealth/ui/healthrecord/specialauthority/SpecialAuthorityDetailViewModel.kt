package ca.bc.gov.bchealth.ui.healthrecord.specialauthority

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ca.bc.gov.bchealth.R
import ca.bc.gov.common.utils.dateString
import ca.bc.gov.repository.specialauthority.SpecialAuthorityRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SpecialAuthorityDetailViewModel @Inject constructor(
    private val specialAuthorityRepository: SpecialAuthorityRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SpecialAuthorityDetailUiState())
    val uiState: StateFlow<SpecialAuthorityDetailUiState> = _uiState.asStateFlow()

    fun getSpecialAuthorityDetails(specialAuthorityId: Long) = viewModelScope.launch {
        try {
            val specialAuthorityDetailItems: MutableList<SpecialAuthorityDetailItem> =
                mutableListOf()

            val specialAuthorityDto =
                specialAuthorityRepository.getSpecialAuthorityDetails(specialAuthorityId)
            specialAuthorityDetailItems.add(
                SpecialAuthorityDetailItem(
                    R.string.status,
                    specialAuthorityDto?.requestStatus ?: "--"
                )
            )
            val prescriberName =
                if (specialAuthorityDto?.prescriberFirstName.isNullOrBlank().not() &&
                    specialAuthorityDto?.prescriberLastName.isNullOrBlank().not()
                ) {
                    "${specialAuthorityDto?.prescriberFirstName} ${specialAuthorityDto?.prescriberLastName}"
                } else {
                    "--"
                }
            specialAuthorityDetailItems.add(
                SpecialAuthorityDetailItem(
                    R.string.prescriber_name, prescriberName
                )
            )
            specialAuthorityDetailItems.add(
                SpecialAuthorityDetailItem(
                    R.string.effective_date,
                    specialAuthorityDto?.effectiveDate?.dateString() ?: "--"
                )
            )
            specialAuthorityDetailItems.add(
                SpecialAuthorityDetailItem(
                    R.string.expiry_date,
                    specialAuthorityDto?.expiryDate?.dateString() ?: "--"
                )
            )
            specialAuthorityDetailItems.add(
                SpecialAuthorityDetailItem(
                    R.string.reference_number,
                    specialAuthorityDto?.referenceNumber ?: "--"
                )
            )

            _uiState.update { state ->
                state.copy(
                    onLoading = false,
                    toolbarTitle = specialAuthorityDto?.drugName,
                    parentEntryId = specialAuthorityDto?.referenceNumber,
                    specialAuthorityDetailItems = specialAuthorityDetailItems
                )
            }
        } catch (e: Exception) {
            _uiState.update { state ->
                state.copy(
                    onLoading = false
                )
            }
        }
    }
}

data class SpecialAuthorityDetailUiState(
    val onLoading: Boolean = false,
    val toolbarTitle: String? = "",
    val parentEntryId: String? = null,
    val specialAuthorityDetailItems: List<SpecialAuthorityDetailItem> = emptyList()
)

data class SpecialAuthorityDetailItem(
    val title: Int,
    val desc: String? = "--"
)

package ca.bc.gov.bchealth.ui.healthrecord.medication

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ca.bc.gov.bchealth.R
import ca.bc.gov.bchealth.ui.healthrecord.medication.MedicationDetailsViewModel.Companion.ITEM_VIEW_TYPE_RECORD
import ca.bc.gov.common.model.DispensingPharmacyDto
import ca.bc.gov.common.model.relation.MedicationWithSummaryAndPharmacyDto
import ca.bc.gov.common.utils.toDate
import ca.bc.gov.repository.MedicationRecordRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/*
* Created by amit_metri on 16,February,2022
*/
@HiltViewModel
class MedicationDetailsViewModel @Inject constructor(
    private val medicationRecordRepository: MedicationRecordRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(MedicationDetailUiState())
    val uiState: StateFlow<MedicationDetailUiState> = _uiState.asStateFlow()

    fun getMedicationDetails(medicationId: Long) = viewModelScope.launch {
        try {
            _uiState.update {
                it.copy(onLoading = true)
            }
            val medicationWithSummaryAndPharmacyDto = medicationRecordRepository
                .getMedicationWithSummaryAndPharmacy(medicationId)

            _uiState.update {
                it.copy(
                    onLoading = false,
                    medicationDetails = prePareMedicationDetails(medicationWithSummaryAndPharmacyDto),
                    toolbarTitle = if (medicationWithSummaryAndPharmacyDto.medicationSummary.isPharmacistAssessment) {
                        medicationWithSummaryAndPharmacyDto.medicationSummary.title
                    } else {
                        medicationWithSummaryAndPharmacyDto.medicationSummary.brandName
                    },
                    parentEntryId = medicationWithSummaryAndPharmacyDto.medicationRecord.prescriptionIdentifier
                )
            }
        } catch (e: Exception) {
            _uiState.update {
                it.copy(
                    onLoading = false,
                    onError = true
                )
            }
        }
    }

    private fun prePareMedicationDetails(
        medicationWithSummaryAndPharmacyDto: MedicationWithSummaryAndPharmacyDto
    ): List<MedicationDetail> {
        val medicationDetails = mutableListOf<MedicationDetail>()
        medicationDetails.add(
            MedicationDetail(
                R.string.practitioner,
                medicationWithSummaryAndPharmacyDto.medicationRecord.practitionerSurname
            )
        )

        if (medicationWithSummaryAndPharmacyDto.medicationSummary.isPharmacistAssessment) {
            medicationDetails.add(
                MedicationDetail(
                    R.string.service_type,
                    medicationWithSummaryAndPharmacyDto.medicationSummary.subtitle
                )
            )
        }

        if (!medicationWithSummaryAndPharmacyDto.medicationSummary.isPharmacistAssessment) {
            medicationDetails.add(
                MedicationDetail(
                    R.string.quantity,
                    medicationWithSummaryAndPharmacyDto.medicationSummary.quantity.toString()
                )
            )
            medicationDetails.add(
                MedicationDetail(
                    R.string.strength,
                    medicationWithSummaryAndPharmacyDto.medicationSummary.strength.toString()
                        .plus(" ")
                        .plus(medicationWithSummaryAndPharmacyDto.medicationSummary.strengthUnit)
                )
            )
            medicationDetails.add(
                MedicationDetail(
                    R.string.form,
                    medicationWithSummaryAndPharmacyDto.medicationSummary.form
                )
            )
            medicationDetails.add(
                MedicationDetail(
                    R.string.manufacturer,
                    medicationWithSummaryAndPharmacyDto.medicationSummary.manufacturer
                )
            )
        }
        medicationDetails.add(
            MedicationDetail(
                if (medicationWithSummaryAndPharmacyDto.medicationSummary.isPin) R.string.pin else
                    R.string.din,
                medicationWithSummaryAndPharmacyDto.medicationSummary.din
            )
        )

        medicationDetails.add(
            MedicationDetail(
                if (medicationWithSummaryAndPharmacyDto.medicationSummary.isPharmacistAssessment) {
                    R.string.pharmacy
                } else {
                    R.string.filled_at
                },
                medicationWithSummaryAndPharmacyDto.dispensingPharmacy.name
            )
        )
        if (!medicationWithSummaryAndPharmacyDto.medicationSummary.isPharmacistAssessment) {
            medicationDetails.add(
                MedicationDetail(
                    R.string.filled_date,
                    medicationWithSummaryAndPharmacyDto.medicationRecord.dispenseDate.toDate()
                )
            )
        }
        medicationDetails.add(
            MedicationDetail(
                R.string.address,
                getAddress(medicationWithSummaryAndPharmacyDto.dispensingPharmacy)
            )
        )
        if (!medicationWithSummaryAndPharmacyDto.medicationSummary.isPharmacistAssessment) {
            medicationDetails.add(
                MedicationDetail(
                    R.string.phone_number,
                    medicationWithSummaryAndPharmacyDto.dispensingPharmacy.phoneNumber
                )
            )
            medicationDetails.add(
                MedicationDetail(
                    R.string.fax,
                    medicationWithSummaryAndPharmacyDto.dispensingPharmacy.faxNumber
                )
            )
            medicationDetails.add(
                MedicationDetail(
                    R.string.direction_for_use,
                    medicationWithSummaryAndPharmacyDto.medicationRecord.directions,
                    ITEM_VIEW_TYPE_DIRECTIONS
                )
            )
        }

        if (medicationWithSummaryAndPharmacyDto.medicationSummary.isPharmacistAssessment) {
            medicationDetails.add(
                MedicationDetail(
                    title = R.string.outcome,
                    description = null,
                    descriptionRes =
                    if (medicationWithSummaryAndPharmacyDto.medicationSummary.prescriptionProvided) {
                        R.string.prescription_provided
                    } else {
                        R.string.prescription_not_provided
                    },
                    viewType = ITEM_VIEW_TYPE_OUTCOME,
                    additionalDetail = if (medicationWithSummaryAndPharmacyDto.medicationSummary.redirectedToHealthCareProvider) {
                        R.string.outcome_description
                    } else {
                        0
                    }
                )
            )
        }
        return medicationDetails
    }

    private fun getAddress(dispensingPharmacy: DispensingPharmacyDto): String {
        var address = ""
        if (!dispensingPharmacy.addressLine1.isNullOrBlank()) {
            address = address.plus(dispensingPharmacy.addressLine1)
        }
        if (!dispensingPharmacy.addressLine2.isNullOrBlank()) {
            address = address.plus(", ").plus(dispensingPharmacy.addressLine2)
        }
        if (!dispensingPharmacy.city.isNullOrBlank()) {
            address = address.plus(", ").plus(dispensingPharmacy.city)
        }
        if (!dispensingPharmacy.province.isNullOrBlank()) {
            address = address.plus(", ").plus(dispensingPharmacy.province)
        }
        return address
    }

    fun resetUiState() {
        _uiState.update {
            it.copy(
                onLoading = false,
                onError = false,
                medicationDetails = null,
                toolbarTitle = null,
            )
        }
    }

    companion object {
        const val ITEM_VIEW_TYPE_RECORD = 0
        const val ITEM_VIEW_TYPE_DIRECTIONS = 1
        const val ITEM_VIEW_TYPE_COMMENTS_COUNT = 2
        const val ITEM_VIEW_TYPE_COMMENTS = 3
        const val ITEM_VIEW_TYPE_OUTCOME = 4
    }
}

data class MedicationDetailUiState(
    val onLoading: Boolean = false,
    val onError: Boolean = false,
    val medicationDetails: List<MedicationDetail>? = null,
    val toolbarTitle: String? = null,
    val parentEntryId: String? = null
)

data class MedicationDetail(
    val title: Int,
    val description: String?,
    val viewType: Int = ITEM_VIEW_TYPE_RECORD,
    @StringRes val descriptionRes: Int = 0,
    @StringRes val additionalDetail: Int = 0
)

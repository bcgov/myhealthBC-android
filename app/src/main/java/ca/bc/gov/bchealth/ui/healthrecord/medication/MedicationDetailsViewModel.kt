package ca.bc.gov.bchealth.ui.healthrecord.medication

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ca.bc.gov.bchealth.ui.healthrecord.medication.MedicationDetailsViewModel.Companion.ITEM_VIEW_TYPE_RECORD
import ca.bc.gov.common.model.DispensingPharmacyDto
import ca.bc.gov.common.model.relation.MedicationWithSummaryAndPharmacyDto
import ca.bc.gov.common.utils.toDate
import ca.bc.gov.common.utils.toLocalDateTimeInstant
import ca.bc.gov.repository.CommentRepository
import ca.bc.gov.repository.MedicationRecordRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.Instant
import javax.inject.Inject

/*
* Created by amit_metri on 16,February,2022
*/
@HiltViewModel
class MedicationDetailsViewModel @Inject constructor(
    private val medicationRecordRepository: MedicationRecordRepository,
    private val commentRepository: CommentRepository
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

            val comment =
                commentRepository
                    .getComments(
                        medicationWithSummaryAndPharmacyDto.medicationRecord.prescriptionIdentifier
                    )
            val comments = mutableListOf<Comment>()
            if (comment.isNotEmpty()) {
                comments.add(Comment("${comment.size} Comments", Instant.now()))
                comments.addAll(comment.map { Comment(it.text, it.createdDateTime?.toLocalDateTimeInstant()) })
            }
            _uiState.update {
                it.copy(
                    onLoading = false,
                    medicationDetails = prePareMedicationDetails(medicationWithSummaryAndPharmacyDto),
                    toolbarTitle = medicationWithSummaryAndPharmacyDto.medicationSummary.brandName,
                    comments = comments
                )
            }
        } catch (e: Exception) {
            _uiState.update {
                it.copy(
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
                "Practitioner:",
                medicationWithSummaryAndPharmacyDto.medicationRecord.practitionerSurname
            )
        )
        medicationDetails.add(
            MedicationDetail(
                "Quantity:",
                medicationWithSummaryAndPharmacyDto.medicationSummary.quantity.toInt().toString()
            )
        )
        medicationDetails.add(
            MedicationDetail(
                "Strength:",
                medicationWithSummaryAndPharmacyDto.medicationSummary.strength.toString()
                    .plus(" ")
                    .plus(medicationWithSummaryAndPharmacyDto.medicationSummary.strengthUnit)
            )
        )
        medicationDetails.add(
            MedicationDetail(
                "Form:",
                medicationWithSummaryAndPharmacyDto.medicationSummary.form
            )
        )
        medicationDetails.add(
            MedicationDetail(
                "Manufacturer:",
                medicationWithSummaryAndPharmacyDto.medicationSummary.manufacturer
            )
        )
        medicationDetails.add(
            MedicationDetail(
                "DIN:",
                medicationWithSummaryAndPharmacyDto.medicationSummary.din
            )
        )
        medicationDetails.add(
            MedicationDetail(
                "Filled at:",
                medicationWithSummaryAndPharmacyDto.dispensingPharmacy.name
            )
        )
        medicationDetails.add(
            MedicationDetail(
                "Filled date:",
                medicationWithSummaryAndPharmacyDto.medicationRecord.dispenseDate.toDate()
            )
        )
        medicationDetails.add(
            MedicationDetail(
                "Address:",
                getAddress(medicationWithSummaryAndPharmacyDto.dispensingPharmacy)
            )
        )
        medicationDetails.add(
            MedicationDetail(
                "Phone Number:",
                medicationWithSummaryAndPharmacyDto.dispensingPharmacy.phoneNumber
            )
        )
        medicationDetails.add(
            MedicationDetail(
                "Fax:",
                medicationWithSummaryAndPharmacyDto.dispensingPharmacy.faxNumber
            )
        )
        medicationDetails.add(
            MedicationDetail(
                "Direction for use",
                medicationWithSummaryAndPharmacyDto.medicationRecord.directions,
                ITEM_VIEW_TYPE_DIRECTIONS
            )
        )
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

    companion object {
        const val ITEM_VIEW_TYPE_RECORD = 0
        const val ITEM_VIEW_TYPE_DIRECTIONS = 1
        const val ITEM_VIEW_TYPE_COMMENTS_COUNT = 2
        const val ITEM_VIEW_TYPE_COMMENTS = 3
    }
}

data class MedicationDetailUiState(
    val onLoading: Boolean = false,
    val onError: Boolean = false,
    val medicationDetails: List<MedicationDetail>? = null,
    val toolbarTitle: String? = null,
    val comments: List<Comment> = emptyList()
)

data class MedicationDetail(
    val title: String,
    val description: String?,
    val viewType: Int = ITEM_VIEW_TYPE_RECORD
)

data class Comment(
    val text: String?,
    val date: Instant?,
)

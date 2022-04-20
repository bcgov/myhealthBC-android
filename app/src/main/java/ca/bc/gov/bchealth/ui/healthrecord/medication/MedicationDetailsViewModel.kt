package ca.bc.gov.bchealth.ui.healthrecord.medication

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ca.bc.gov.bchealth.ui.healthrecord.medication.MedicationDetailsViewModel.Companion.ITEM_VIEW_TYPE_RECORD
import ca.bc.gov.common.model.relation.MedicationWithSummaryAndPharmacyDto
import ca.bc.gov.common.utils.toDate
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
            var userProfileId : String? = null
            val comments = mutableListOf<Comment>()
            if (comment.isNotEmpty()) {
                userProfileId = comment.first().userProfileId
                comments.add(Comment("${comment.size} Comments", Instant.now()))
                comments.addAll(comment.map { Comment(it.text, it.createdDateTime) })
            }
            _uiState.update {
                it.copy(
                    onLoading = false,
                    medicationDetails = prePareMedicationDetails(medicationWithSummaryAndPharmacyDto),
                    toolbarTitle = medicationWithSummaryAndPharmacyDto.medicationSummary.brandName,
                    comments = comments,
                    userProfileId = userProfileId
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
                medicationWithSummaryAndPharmacyDto.medicationSummary.quantity.toString()
            )
        )
        medicationDetails.add(
            MedicationDetail(
                "Strength:",
                medicationWithSummaryAndPharmacyDto.medicationSummary.strength.toString()
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
                "Din:",
                medicationWithSummaryAndPharmacyDto.medicationSummary.din
            )
        )
        medicationDetails.add(
            MedicationDetail(
                "Filled at:",
                medicationWithSummaryAndPharmacyDto.medicationSummary.genericName
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
                medicationWithSummaryAndPharmacyDto.dispensingPharmacy.addressLine1
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

    fun addComment(medicationId: Long, userProfileId: String?, comment: String) = viewModelScope.launch {
        try {
            _uiState.update {
                it.copy(onLoading = true)
            }
            val medicationWithSummaryAndPharmacyDto =
                medicationRecordRepository.getMedicationWithSummaryAndPharmacy(medicationId)

            val comment = commentRepository.addComment(
                        medicationWithSummaryAndPharmacyDto.medicationRecord.prescriptionIdentifier, userProfileId, comment
                    )
            val comments = mutableListOf<Comment>()
            if (comment.isNotEmpty()) {
                comments.add(Comment("${comment.size} Comments", Instant.now()))
                comments.addAll(comment.map { Comment(it.text, it.createdDateTime) })
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
            e.printStackTrace()
            Log.i("RASHMI", "Exception: ${e.message}")
            _uiState.update {
                it.copy(
                    onError = true
                )
            }
        }
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
    val comments: List<Comment> = emptyList(),
    val userProfileId: String? = null
)

data class MedicationDetail(
    val title: String,
    val description: String? = "N/A",
    val viewType: Int = ITEM_VIEW_TYPE_RECORD
)

data class Comment(
    val text: String?,
    val date: Instant,
)

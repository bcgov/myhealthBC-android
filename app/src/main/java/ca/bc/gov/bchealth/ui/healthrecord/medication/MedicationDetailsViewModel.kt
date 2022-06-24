package ca.bc.gov.bchealth.ui.healthrecord.medication

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ca.bc.gov.bchealth.R
import ca.bc.gov.bchealth.ui.healthrecord.medication.MedicationDetailsViewModel.Companion.ITEM_VIEW_TYPE_RECORD
import ca.bc.gov.common.exceptions.NetworkConnectionException
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

    private val _commentState = MutableStateFlow(CommentUiState())
    val commentState: StateFlow<CommentUiState> = _commentState.asStateFlow()

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
                    toolbarTitle = medicationWithSummaryAndPharmacyDto.medicationSummary.brandName,
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

    fun fetchComments() = viewModelScope.launch {
        if (_uiState.value.parentEntryId != null) {
            val parentEntryId = _uiState.value.parentEntryId

            try {
                _commentState.update {
                    it.copy(onLoading = true)
                }
                val comments =
                    commentRepository
                        .getLocalComments(
                            parentEntryId
                        )
                val commentsTemp = mutableListOf<Comment>()
                if (comments.isNotEmpty()) {
                    commentsTemp.add(Comment(parentEntryId, "${comments.size}", Instant.now()))
                    val firsComment = comments.maxByOrNull { it.createdDateTime }
                    commentsTemp.add(
                        Comment(
                            firsComment?.parentEntryId,
                            firsComment?.text,
                            firsComment?.createdDateTime?.toLocalDateTimeInstant(),
                            firsComment?.isUploaded ?: true
                        )
                    )
                }

                _commentState.update {
                    it.copy(
                        onLoading = false,
                        comments = commentsTemp
                    )
                }
            } catch (e: Exception) {
                when (e) {
                    is NetworkConnectionException -> {
                        _commentState.update { state ->
                            state.copy(
                                onLoading = false,
                                isConnected = false
                            )
                        }
                    }
                    else -> {
                        _commentState.update {
                            it.copy(
                                onLoading = false,
                                onError = true
                            )
                        }
                    }
                }
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
        medicationDetails.add(
            MedicationDetail(
                if (medicationWithSummaryAndPharmacyDto.medicationSummary.isPin) R.string.pin else
                    R.string.din,
                medicationWithSummaryAndPharmacyDto.medicationSummary.din
            )
        )
        medicationDetails.add(
            MedicationDetail(
                R.string.filled_at,
                medicationWithSummaryAndPharmacyDto.dispensingPharmacy.name
            )
        )
        medicationDetails.add(
            MedicationDetail(
                R.string.filled_date,
                medicationWithSummaryAndPharmacyDto.medicationRecord.dispenseDate.toDate()
            )
        )
        medicationDetails.add(
            MedicationDetail(
                R.string.address,
                getAddress(medicationWithSummaryAndPharmacyDto.dispensingPharmacy)
            )
        )
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

    fun addComment(comment: String, entryTypeCode: String) = viewModelScope.launch {
        try {
            _uiState.update {
                it.copy(onLoading = true)
            }

            val comments = commentRepository.addComment(
                _uiState.value.parentEntryId, comment, entryTypeCode
            )
            val commentsTemp = mutableListOf<Comment>()
            if (comments.isNotEmpty()) {
                commentsTemp.add(
                    Comment(
                        _uiState.value.parentEntryId,
                        comments.size.toString(),
                        Instant.now(),
                        true
                    )
                )
                val firsComment = comments.maxByOrNull { it.createdDateTime }
                commentsTemp.add(
                    Comment(
                        firsComment?.parentEntryId,
                        firsComment?.text,
                        firsComment?.createdDateTime?.toLocalDateTimeInstant(),
                        firsComment?.isUploaded ?: true
                    )
                )
            }
            _uiState.update {
                it.copy(
                    onLoading = false,
                    comments = commentsTemp
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
            _uiState.update {
                it.copy(
                    onError = true,
                    onLoading = false
                )
            }
        }
    }

    fun resetUiState() {
        _uiState.update {
            it.copy(
                onLoading = false,
                onError = false,
                medicationDetails = null,
                toolbarTitle = null,
                comments = emptyList()
            )
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
    val parentEntryId: String? = null
)

data class MedicationDetail(
    val title: Int,
    val description: String?,
    val viewType: Int = ITEM_VIEW_TYPE_RECORD
)

data class Comment(
    val parentEntryId: String?,
    val text: String?,
    val date: Instant?,
    val isUploaded: Boolean = true
)

data class CommentUiState(
    val onLoading: Boolean = false,
    val onError: Boolean = false,
    val comments: List<Comment> = emptyList(),
    val isConnected: Boolean = true
)

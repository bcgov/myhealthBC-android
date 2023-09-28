package ca.bc.gov.bchealth.ui.comment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ca.bc.gov.bchealth.R
import ca.bc.gov.bchealth.model.mapper.toUiModel
import ca.bc.gov.common.const.AUTH_ERROR_DO_LOGIN
import ca.bc.gov.common.exceptions.MyHealthException
import ca.bc.gov.common.model.SyncStatus
import ca.bc.gov.common.model.comment.CommentDto
import ca.bc.gov.common.utils.toLocalDateTimeInstant
import ca.bc.gov.repository.CommentRepository
import ca.bc.gov.repository.bcsc.BcscAuthRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.Instant
import javax.inject.Inject

/*
* Created by amit_metri on 18,April,2022
*/
@HiltViewModel
class CommentsViewModel @Inject constructor(
    private val commentRepository: CommentRepository,
    private val bcscAuthRepo: BcscAuthRepo
) : ViewModel() {

    private val _uiState = MutableStateFlow(CommentsUiState())
    val uiState: StateFlow<CommentsUiState> = _uiState.asStateFlow()

    fun getComments(parentEntryId: String) = viewModelScope.launch {
        onLoading()

        try {
            val commentsDtoList = commentRepository.getLocalComments(parentEntryId)
            val commentsSummary = getCommentsSummary(commentsDtoList, parentEntryId)

            _uiState.update { it ->
                it.copy(
                    onLoading = false,
                    commentsList = commentsDtoList.map { it.toUiModel() },
                    commentsSummary = commentsSummary
                )
            }
        } catch (e: Exception) {
            handleException(e)
        }
    }

    fun addComment(parentEntryId: String, comment: String, entryTypeCode: String) =
        viewModelScope.launch {
            try {
                onLoading()

                val commentsDtoList = commentRepository.addComment(
                    parentEntryId,
                    comment,
                    entryTypeCode
                )

                val commentsSummary = getCommentsSummary(commentsDtoList, parentEntryId)

                val isBcscSessionActive = bcscAuthRepo.checkSession()

                _uiState.update { it ->
                    it.copy(
                        onLoading = false,
                        commentsList = commentsDtoList.map { it.toUiModel() },
                        commentsSummary = commentsSummary,
                        onCommentsUpdated = true,
                        isBcscSessionActive = isBcscSessionActive
                    )
                }
            } catch (e: Exception) {
                handleException(e)
            }
        }

    fun toggleEditMode(isEditMode: Boolean) {
        _uiState.update {
            it.copy(displayEditLayout = isEditMode)
        }
    }

    fun updateComment(parentEntryId: String, comment: Comment) = viewModelScope.launch {
        comment.id ?: return@launch

        try {
            onLoading()

            val comments = commentRepository.enqueueEditComment(
                comment.id,
                comment.text.orEmpty(),
                parentEntryId
            )

            val isBcscSessionActive = bcscAuthRepo.checkSession()

            _uiState.update { it ->
                it.copy(
                    onLoading = false,
                    displayEditLayout = false,
                    commentsList = comments.map { it.toUiModel() },
                    commentsSummary = getCommentsSummary(comments, parentEntryId),
                    onCommentsUpdated = true,
                    isBcscSessionActive = isBcscSessionActive
                )
            }
        } catch (e: Exception) {
            handleException(e)
        }
    }

    fun deleteComment(parentEntryId: String, comment: Comment) = viewModelScope.launch {
        comment.id ?: return@launch

        try {
            onLoading()

            val comments = commentRepository.enqueueDeleteComment(comment.id, parentEntryId)

            val isBcscSessionActive = bcscAuthRepo.checkSession()

            _uiState.update {
                it.copy(
                    onLoading = false,
                    displayEditLayout = false,
                    commentsList = comments.map { comment ->
                        comment.toUiModel()
                    },
                    commentsSummary = getCommentsSummary(comments, parentEntryId),
                    onCommentsUpdated = true,
                    isBcscSessionActive = isBcscSessionActive
                )
            }
        } catch (e: Exception) {
            handleException(e)
        }
    }

    private fun getCommentsSummary(
        commentsDtoList: List<CommentDto>,
        parentEntryId: String
    ): CommentsSummary? {
        if (commentsDtoList.isEmpty()) return null

        val lastComment = commentsDtoList.first()

        return CommentsSummary(
            text = lastComment.text.orEmpty(),
            date = lastComment.createdDateTime.toLocalDateTimeInstant(),
            entryTypeCode = lastComment.entryTypeCode.orEmpty(),
            syncStatus = lastComment.syncStatus,
            parentEntryId = parentEntryId,
            count = commentsDtoList.size,
        )
    }

    fun resetUiState() {
        _uiState.update {
            it.copy(
                onLoading = false,
                onError = false,
                commentsList = emptyList(),
                commentsSummary = null
            )
        }
    }

    private fun onLoading() {
        _uiState.update { it.copy(onLoading = true) }
    }

    private fun handleException(e: Exception) {
        e.printStackTrace()
        val isBcscSessionActive = (e is MyHealthException && e.errCode == AUTH_ERROR_DO_LOGIN).not()
        _uiState.update {
            it.copy(
                isBcscSessionActive = isBcscSessionActive,
                onError = true,
                onLoading = false
            )
        }
    }
}

data class CommentsUiState(
    val onLoading: Boolean = false,
    val onError: Boolean = false,
    val commentsList: List<Comment>? = null,
    val commentsSummary: CommentsSummary? = null,
    val onCommentsUpdated: Boolean = false,
    val displayEditLayout: Boolean = false,
    val isBcscSessionActive: Boolean? = null,
)

data class Comment(
    val id: String? = null,
    val text: String?,
    val date: Instant?,
    val version: Long,
    val entryTypeCode: String,
    val createdDateTime: Instant,
    val createdBy: String,
    val updatedDateTime: Instant,
    val updatedBy: String,
    val syncStatus: SyncStatus = SyncStatus.UP_TO_DATE,
    var editable: Boolean = false
)

data class CommentsSummary(
    val text: String,
    val date: Instant?,
    val syncStatus: SyncStatus,
    val entryTypeCode: String,
    val parentEntryId: String,
    val count: Int,
)

fun SyncStatus.getDescription(): Int? = when (this) {
    SyncStatus.UP_TO_DATE -> null
    SyncStatus.INSERT -> R.string.posting
    SyncStatus.EDIT -> R.string.posting
    SyncStatus.DELETE -> R.string.deleting
}

enum class CommentEntryTypeCode(val value: String) {
    MEDICATION("Med"),
    LAB_RESULTS("ALO"),
    COVID_TEST("Lab"),
    HEALTH_VISITS("Enc"),
    SPECIAL_AUTHORITY("SAR"),
    HOSPITAL_VISIT("HOS")
}

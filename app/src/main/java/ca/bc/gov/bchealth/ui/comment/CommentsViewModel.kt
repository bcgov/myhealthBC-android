package ca.bc.gov.bchealth.ui.comment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ca.bc.gov.bchealth.model.mapper.toUiModel
import ca.bc.gov.common.BuildConfig.FLAG_ADD_COMMENTS
import ca.bc.gov.common.model.comment.CommentDto
import ca.bc.gov.repository.CommentRepository
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
    private val commentRepository: CommentRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CommentsUiState())
    val uiState: StateFlow<CommentsUiState> = _uiState.asStateFlow()

    fun getComments(parentEntryId: String) = viewModelScope.launch {
        _uiState.update {
            it.copy(
                onLoading = true
            )
        }

        try {
            val commentsDtoList = commentRepository.getLocalComments(parentEntryId) as MutableList
            commentsDtoList.sortBy { it.createdDateTime }

            // latest comment
            val commentsTemp = getLatestComment(commentsDtoList, parentEntryId)

            _uiState.update { it ->
                it.copy(
                    onLoading = false,
                    commentsList = commentsDtoList.map { it.toUiModel() },
                    latestComment = commentsTemp
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

    fun addComment(parentEntryId: String, comment: String, entryTypeCode: String) =
        viewModelScope.launch {
            try {
                _uiState.update {
                    it.copy(onLoading = true)
                }

                val commentsDtoList = commentRepository.addComment(
                    parentEntryId,
                    comment,
                    entryTypeCode
                ) as MutableList
                commentsDtoList.sortBy { it.createdDateTime }

                // latest comment
                val commentsTemp = getLatestComment(commentsDtoList, parentEntryId)

                _uiState.update { it ->
                    it.copy(
                        onLoading = false,
                        commentsList = commentsDtoList.map { it.toUiModel() },
                        latestComment = commentsTemp,
                        onCommentsUpdated = true
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

    fun toggleEditMode(isEditMode: Boolean) {
        _uiState.update {
            it.copy(displayEditLayout = isEditMode)
        }
    }

    fun updateComment(parentEntryId: String, comment: Comment) =
        viewModelScope.launch {
            comment.id ?: return@launch

            val commentDto = CommentDto(
                id = comment.id,
                userProfileId = null,
                text = comment.text,
                entryTypeCode = comment.entryTypeCode,
                parentEntryId = parentEntryId,
                version = comment.version,
                createdDateTime = Instant.now(),
                createdBy = null,
                updatedDateTime = Instant.now(),
                updatedBy = null,
                isUploaded = false
            )

            try {
                commentRepository.updateComment(commentDto)
            } catch (e: Exception) {
                println(e.printStackTrace())
            }
        }

    private fun getLatestComment(
        commentsDtoList: MutableList<CommentDto>,
        parentEntryId: String
    ): MutableList<Comment> {
        val commentsList = mutableListOf<Comment>()
        if (commentsDtoList.isNotEmpty()) {
            commentsList.add(
                // Item that displays the number of comments instead of the content. Todo: refactor it
                Comment(
                    parentEntryId,
                    "${commentsDtoList.size}",
                    Instant.now(),
                    0L,
                    commentsDtoList.last().entryTypeCode.orEmpty()
                )
            )

            if (FLAG_ADD_COMMENTS) {
                commentsDtoList.lastOrNull()?.let {
                    commentsList.add(it.toUiModel())
                }
            } else {
                commentsList.addAll(
                    commentsDtoList.map {
                        it.toUiModel()
                    }
                )
            }
        }
        return commentsList
    }

    fun resetUiState() {
        _uiState.update {
            it.copy(
                onLoading = false,
                onError = false,
                commentsList = emptyList(),
                latestComment = emptyList()
            )
        }
    }
}

data class CommentsUiState(
    val onLoading: Boolean = false,
    val onError: Boolean = false,
    val commentsList: List<Comment> = emptyList(),
    val latestComment: List<Comment> = emptyList(),
    val onCommentsUpdated: Boolean = false,
    val displayEditLayout: Boolean = false
)

data class Comment(
    val id: String? = null,
    val text: String?,
    val date: Instant?,
    val version: Long,
    val entryTypeCode: String,
    val isUploaded: Boolean = true,
    var editable: Boolean = false
)

enum class CommentEntryTypeCode(val value: String) {
    MEDICATION("Med"),
    LAB_RESULTS("ALO"),
    COVID_TEST("Lab"),
    HEALTH_VISITS("Enc"),
    SPECIAL_AUTHORITY("SAR"),
}

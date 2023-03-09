package ca.bc.gov.bchealth.ui.comment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ca.bc.gov.common.BuildConfig.FLAG_ADD_COMMENTS
import ca.bc.gov.common.model.comment.CommentDto
import ca.bc.gov.common.utils.toLocalDateTimeInstant
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
                    commentsList = commentsDtoList.map {
                        Comment(
                            it.id,
                            it.text,
                            it.createdDateTime.toLocalDateTimeInstant(),
                            it.isUploaded
                        )
                    },
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
                        commentsList = commentsDtoList.map {
                            Comment(
                                it.id,
                                it.text,
                                it.createdDateTime.toLocalDateTimeInstant(),
                                it.isUploaded
                            )
                        },
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

    private fun getLatestComment(
        commentsDtoList: MutableList<CommentDto>,
        parentEntryId: String
    ): MutableList<Comment> {
        val commentsList = mutableListOf<Comment>()
        if (commentsDtoList.isNotEmpty()) {
            commentsList.add(
                Comment(
                    parentEntryId,
                    "${commentsDtoList.size}",
                    Instant.now()
                )
            )

            if (FLAG_ADD_COMMENTS) {
                val latestComment = commentsDtoList.lastOrNull()
                commentsList.add(
                    Comment(
                        latestComment?.parentEntryId,
                        latestComment?.text,
                        latestComment?.createdDateTime?.toLocalDateTimeInstant(),
                        latestComment?.isUploaded ?: true
                    )
                )
            } else {
                commentsList.addAll(
                    commentsDtoList.map {
                        Comment(
                            it.parentEntryId,
                            it.text,
                            it.createdDateTime?.toLocalDateTimeInstant(),
                            it.isUploaded ?: true
                        )
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

package ca.bc.gov.bchealth.ui.comment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ca.bc.gov.common.utils.toLocalDateTimeInstant
import ca.bc.gov.repository.CommentRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.lang.Exception
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
            commentsDtoList.sortByDescending { it.createdDateTime }
            _uiState.update { it ->
                it.copy(
                    onLoading = false,
                    commentsList = commentsDtoList.map {
                        Comment(
                            it.id,
                            it.text,
                            it.createdDateTime.toLocalDateTimeInstant()
                        )
                    }
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

                val commentsDtoList = commentRepository.addComment(parentEntryId, comment, entryTypeCode) as MutableList
                commentsDtoList.sortByDescending { it.createdDateTime }
                _uiState.update { it ->
                    it.copy(
                        onLoading = false,
                        commentsList = commentsDtoList.map {
                            Comment(
                                it.id,
                                it.text,
                                it.createdDateTime.toLocalDateTimeInstant()
                            )
                        }
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
                commentsList = null
            )
        }
    }
}

data class CommentsUiState(
    val onLoading: Boolean = false,
    val onError: Boolean = false,
    val commentsList: List<Comment>? = null
)

data class Comment(
    val id: String,
    val text: String?,
    val date: Instant?
)

enum class CommentEntryTypeCode(val value: String) {
    MEDICATION("Med")
}

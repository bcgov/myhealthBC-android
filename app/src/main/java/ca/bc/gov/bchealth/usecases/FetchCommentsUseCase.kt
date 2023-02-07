package ca.bc.gov.bchealth.usecases

import ca.bc.gov.bchealth.usecases.records.BaseRecordUseCase
import ca.bc.gov.common.BuildConfig
import ca.bc.gov.common.model.AuthParametersDto
import ca.bc.gov.common.model.comment.CommentDto
import ca.bc.gov.repository.CommentRepository
import ca.bc.gov.repository.di.IoDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

class FetchCommentsUseCase @Inject constructor(
    private val commentsRepository: CommentRepository,
    @IoDispatcher private val dispatcher: CoroutineDispatcher,
) : BaseRecordUseCase(dispatcher) {

    suspend fun execute(authParameters: AuthParametersDto) {
        if (BuildConfig.FLAG_ADD_COMMENTS.not()) return

        val comments: List<CommentDto>? = fetchRecord(
            authParameters, commentsRepository::getComments
        )

        insertComments(comments)
    }

    private suspend fun insertComments(comments: List<CommentDto>?) {
        commentsRepository.delete(true)
        comments?.let { commentsRepository.insert(it) }
    }
}

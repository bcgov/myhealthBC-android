package ca.bc.gov.bchealth.usecases

import ca.bc.gov.common.model.AuthParametersDto
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

abstract class BaseRecordUseCase(private val dispatcher: CoroutineDispatcher) {

    protected suspend fun <T> fetchRecord(
        authParameters: AuthParametersDto,
        action: suspend (String, String) -> T?
    ): T? {
        var response: T?
        withContext(dispatcher) {
            response = action.invoke(authParameters.token, authParameters.hdid)
        }
        return response
    }
}
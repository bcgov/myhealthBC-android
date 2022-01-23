package ca.bc.gov.data.utils

import ca.bc.gov.common.const.SERVER_ERROR
import ca.bc.gov.common.exceptions.MustBeQueuedException
import ca.bc.gov.common.exceptions.MyHealthNetworkException
import retrofit2.Response

suspend inline fun <T> safeCall(crossinline responseFun: suspend () -> Response<T>): T? {

    return try {
        val result = responseFun.invoke()

        if (result.isSuccessful) {
            result.body()
        } else {
            throw MyHealthNetworkException(
                errCode = SERVER_ERROR,
                message = result.errorBody()?.toString()
            )
        }
    } catch (e: Exception) {
        when (e) {
            is MustBeQueuedException -> {
                throw e
            }
            else -> {
                throw MyHealthNetworkException(errCode = SERVER_ERROR, message = e.message)
            }
        }
    }
}

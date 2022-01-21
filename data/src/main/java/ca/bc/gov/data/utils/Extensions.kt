package ca.bc.gov.data.utils

import ca.bc.gov.common.exceptions.MustBeQueuedException
import ca.bc.gov.common.exceptions.MyHealthException
import retrofit2.Response

suspend inline fun <T> safeCall(crossinline responseFun: suspend () -> Response<T>): T? {

    return try {
        val result = responseFun.invoke()

        if (result.isSuccessful) {
            result.body()
        } else {
            throw MyHealthException(1000, result.errorBody()?.toString())
        }
    } catch (e: Exception) {
        when (e) {
            is MustBeQueuedException -> {
                throw e
            }
            else -> {
                throw MyHealthException(1000, e.message)
            }
        }
    }
}

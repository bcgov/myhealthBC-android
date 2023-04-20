package ca.bc.gov.data.utils

import ca.bc.gov.common.const.SERVER_ERROR
import ca.bc.gov.common.exceptions.MyHealthException
import ca.bc.gov.common.exceptions.NetworkConnectionException
import ca.bc.gov.common.exceptions.ProtectiveWordException
import ca.bc.gov.data.datasource.remote.model.base.ApiError
import com.google.gson.Gson
import retrofit2.Response

suspend inline fun <T> safeCall(crossinline responseFun: suspend () -> Response<T>): T? {

    return try {
        val result = responseFun.invoke()

        if (result.isSuccessful) {
            result.body()
        } else {
            val error = Gson().fromJson(result.errorBody()?.string(), ApiError::class.java)
            throw MyHealthException(
                errCode = SERVER_ERROR,
                message = error.toString()
            )
        }
    } catch (e: Exception) {
        when (e) {
            is NetworkConnectionException -> {
                throw e
            }
            is ProtectiveWordException -> {
                throw e
            }
            else -> {
                throw MyHealthException(errCode = SERVER_ERROR, message = e.message)
            }
        }
    }
}

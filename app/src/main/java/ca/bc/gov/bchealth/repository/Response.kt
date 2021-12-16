package ca.bc.gov.bchealth.repository

/*
* Created by amit_metri on 19,October,2021
*/
sealed class Response<T>(val data: Any? = null, val errorData: ErrorData? = null) {
    class Loading<T> : Response<T>()
    class Success<T>(data: Any? = null) : Response<T>(data = data)
    class Error<T>(errorData: ErrorData?) : Response<T>(errorData = errorData)
}

package ca.bc.gov.common.model

data class ResultStatus<T>(val data: T, val status: ResultStatusType)

enum class ResultStatusType {
    SUCCESS, DATE_ERROR, GENERIC_FAILURE
}

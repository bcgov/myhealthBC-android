package ca.bc.gov.common.model

class ResultStatus<T>(
    val data: T,
    val type: ResultStatusType = ResultStatusType.SUCCESS
)

enum class ResultStatusType {
    SUCCESS,
    DATE_ERROR,
    TITLE_ERROR
}
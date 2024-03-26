package ca.bc.gov.common.exceptions

//check handlers
sealed class PartialRecordsException : Exception() {
    class DateError : PartialRecordsException()
    class TitleError : PartialRecordsException()
}

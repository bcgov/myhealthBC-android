package ca.bc.gov.common.model

enum class DataSource(val source: Int) {
    QR_CODE(1),
    PUBLIC_API(2),
    BCSC(3),
    OTHER(4)
}
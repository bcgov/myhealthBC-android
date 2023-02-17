package ca.bc.gov.bchealth.ui.healthrecord

data class HealthRecordDetailItem(
    val title: Int,
    val description: String?,
    val placeholder : Int? = null,
    val footer: Int? = null,
)

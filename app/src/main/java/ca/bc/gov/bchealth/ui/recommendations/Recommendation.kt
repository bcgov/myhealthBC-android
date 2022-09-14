package ca.bc.gov.bchealth.ui.recommendations

data class Recommendation(
    val title: String,
    val status: String?,
    val date: String,
    var fullContent: Boolean = false,
)

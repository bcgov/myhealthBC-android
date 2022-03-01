package ca.bc.gov.common.model.labtest

/**
 * @author Pinakin Kansara
 */
data class LabTestDto(
    val id: Long,
    val labOrderId: String,
    val obxId: String? = null,
    val batteryType: String? = null,
    val outOfRange: Boolean = false,
    val loinc: String? = null,
    val testStatus: String? = null,
)

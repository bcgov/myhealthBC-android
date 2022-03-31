package ca.bc.gov.data.datasource.remote.model.base.labtest

/**
 * @author Pinakin Kansara
 */
data class LaboratoryTest(
    val batteryType: String?,
    val obxId: String?,
    val outOfRange: Boolean,
    val loinc: String?,
    val testStatus: String?
)

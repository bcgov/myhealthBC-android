package ca.bc.gov.common.model.patient

/**
 * @author Pinakin Kansara
 */
data class PatientWithHealthRecordCount(
    val patient: Patient,
    val vaccineRecordCount: Int,
    val testResultCount: Int
)

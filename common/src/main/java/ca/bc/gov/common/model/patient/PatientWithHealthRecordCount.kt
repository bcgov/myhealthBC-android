package ca.bc.gov.common.model.patient

/**
 * @author Pinakin Kansara
 */
data class PatientWithHealthRecordCount(
    val patientDto: PatientDto,
    val vaccineRecordCount: Int,
    val testResultCount: Int
)

package ca.bc.gov.common.model.config

/**
 * @author pinakin.kansara
 * Created 2023-12-05 at 10:18 a.m.
 */

sealed class DataSetFeatureFlag(private val flags: Set<String>) {
    fun isClinicalDocumentEnabled(): Boolean = flags.contains("clinicalDocument")
    fun isCovid19TestResultEnabled(): Boolean = flags.contains("covid19TestResult")
    fun isDiagnosticImagingEnabled(): Boolean = flags.contains("diagnosticImaging")
    fun isHealthVisitEnabled(): Boolean = flags.contains("healthVisit")
    fun isHospitalVisitEnabled(): Boolean = flags.contains("hospitalVisit")
    fun isImmunizationEnabled(): Boolean = flags.contains("immunization")
    fun isLabResultEnabled(): Boolean = flags.contains("labResult")
    fun isMedicationEnabled(): Boolean = flags.contains("medication")
    fun isNoteEnabled(): Boolean = flags.contains("note")
    fun isSpecialAuthorityRequestEnabled(): Boolean = flags.contains("specialAuthorityRequest")
    fun isBcCancerScreeningEnabled(): Boolean = flags.contains("bcCancerScreening")
}

data class PatientDataSetFeatureFLag(private val flags: Set<String>) : DataSetFeatureFlag(flags)
data class DependentDataSetFeatureFLag(private val flags: Set<String>) : DataSetFeatureFlag(flags)

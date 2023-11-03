package ca.bc.gov.bchealth.ui.filter

import androidx.annotation.IdRes
import ca.bc.gov.bchealth.R
import ca.bc.gov.bchealth.ui.healthrecord.HealthRecordType
import ca.bc.gov.bchealth.ui.healthrecord.HealthRecordType.CLINICAL_DOCUMENT_RECORD
import ca.bc.gov.bchealth.ui.healthrecord.HealthRecordType.COVID_TEST_RECORD
import ca.bc.gov.bchealth.ui.healthrecord.HealthRecordType.HEALTH_VISIT_RECORD
import ca.bc.gov.bchealth.ui.healthrecord.HealthRecordType.HOSPITAL_VISITS_RECORD
import ca.bc.gov.bchealth.ui.healthrecord.HealthRecordType.IMMUNIZATION_RECORD
import ca.bc.gov.bchealth.ui.healthrecord.HealthRecordType.LAB_RESULT_RECORD
import ca.bc.gov.bchealth.ui.healthrecord.HealthRecordType.MEDICATION_RECORD
import ca.bc.gov.bchealth.ui.healthrecord.HealthRecordType.SPECIAL_AUTHORITY_RECORD

enum class TimelineTypeFilter(@IdRes val id: Int?, val recordType: HealthRecordType?) {
    ALL(null, null),
    MEDICATION(R.id.chip_medication, MEDICATION_RECORD),
    LAB_RESULT(R.id.chip_lab_results, LAB_RESULT_RECORD),
    COVID_19_TEST(R.id.chip_covid_test, COVID_TEST_RECORD),
    IMMUNIZATION(R.id.chip_immunizations, IMMUNIZATION_RECORD),
    HEALTH_VISIT(R.id.chip_health_visit, HEALTH_VISIT_RECORD),
    SPECIAL_AUTHORITY(R.id.chip_special_authority, SPECIAL_AUTHORITY_RECORD),
    HOSPITAL_VISITS(R.id.chip_hospital_visits, HOSPITAL_VISITS_RECORD),
    CLINICAL_DOCUMENT(R.id.chip_clinical_document, CLINICAL_DOCUMENT_RECORD),
    DIAGNOSTIC_IMAGING(R.id.chip_diagnostic_imaging, HealthRecordType.DIAGNOSTIC_IMAGING);

    companion object {
        fun findByName(name: String): TimelineTypeFilter? = values().find {
            it.name == name
        }

        fun findByFilterValue(filterValue: String): TimelineTypeFilter = when (filterValue) {
            "Medications" -> MEDICATION
            "Laboratory" -> LAB_RESULT
            "COVID19Laboratory" -> COVID_19_TEST
            "Immunization" -> IMMUNIZATION
            "HealthVisit" -> HEALTH_VISIT
            "SpecialAuthority" -> SPECIAL_AUTHORITY
            "HospitalVisit" -> HOSPITAL_VISITS
            "ClinicalDocument" -> CLINICAL_DOCUMENT
            "DiExam",
            "ImagingReports" -> DIAGNOSTIC_IMAGING
            else -> ALL
        }
    }
}

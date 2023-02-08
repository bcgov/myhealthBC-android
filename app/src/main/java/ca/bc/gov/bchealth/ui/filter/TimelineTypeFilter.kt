package ca.bc.gov.bchealth.ui.filter

import androidx.annotation.IdRes
import ca.bc.gov.bchealth.R
import ca.bc.gov.bchealth.ui.healthrecord.individual.HealthRecordType
import ca.bc.gov.bchealth.ui.healthrecord.individual.HealthRecordType.CLINICAL_DOCUMENT_RECORD
import ca.bc.gov.bchealth.ui.healthrecord.individual.HealthRecordType.COVID_TEST_RECORD
import ca.bc.gov.bchealth.ui.healthrecord.individual.HealthRecordType.HEALTH_VISIT_RECORD
import ca.bc.gov.bchealth.ui.healthrecord.individual.HealthRecordType.HOSPITAL_VISITS_RECORD
import ca.bc.gov.bchealth.ui.healthrecord.individual.HealthRecordType.IMMUNIZATION_RECORD
import ca.bc.gov.bchealth.ui.healthrecord.individual.HealthRecordType.LAB_TEST_RECORD
import ca.bc.gov.bchealth.ui.healthrecord.individual.HealthRecordType.MEDICATION_RECORD
import ca.bc.gov.bchealth.ui.healthrecord.individual.HealthRecordType.SPECIAL_AUTHORITY_RECORD

enum class TimelineTypeFilter(@IdRes val id: Int?, val recordType: HealthRecordType?) {
    ALL(null, null),
    MEDICATION(R.id.chip_medication, MEDICATION_RECORD),
    LAB_TEST(R.id.chip_lab_test, LAB_TEST_RECORD),
    COVID_19_TEST(R.id.chip_covid_test, COVID_TEST_RECORD),
    IMMUNIZATION(R.id.chip_immunizations, IMMUNIZATION_RECORD),
    HEALTH_VISIT(R.id.chip_health_visit, HEALTH_VISIT_RECORD),
    SPECIAL_AUTHORITY(R.id.chip_special_authority, SPECIAL_AUTHORITY_RECORD),
    HOSPITAL_VISITS(R.id.chip_hospital_visits, HOSPITAL_VISITS_RECORD),
    CLINICAL_DOCUMENT(R.id.chip_clinical_document, CLINICAL_DOCUMENT_RECORD);

    companion object {
        fun findByName(name: String): TimelineTypeFilter? = values().find {
            it.name == name
        }
    }
}

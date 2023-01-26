package ca.bc.gov.bchealth.ui.filter

import androidx.annotation.IdRes
import ca.bc.gov.bchealth.R

enum class TimelineTypeFilter(@IdRes val id: Int?) {
    ALL(null),
    MEDICATION(R.id.chip_medication),
    LAB_TEST(R.id.chip_lab_test),
    COVID_19_TEST(R.id.chip_covid_test),
    IMMUNIZATION(R.id.chip_immunizations),
    HEALTH_VISIT(R.id.chip_health_visit),
    SPECIAL_AUTHORITY(R.id.chip_special_authority),
    HOSPITAL_VISITS(R.id.chip_hospital_visits)
}

package ca.bc.gov.common.model

enum class AppFeatureName(val value: String) {
    HEALTH_RECORDS("Health records"),
    IMMUNIZATION_SCHEDULES("Immunization schedules"),
    HEALTH_RESOURCES("Health resources"),
    PROOF_OF_VACCINE("Proof of vaccination"),
    SERVICES("Services"),
    IMMUNIZATIONS("Immunizations"),
    MEDICATIONS("Medications"),
    COVID_TESTS("COVID‑19 Tests"),
    IMAGING_REPORTS("Imaging Reports"),
    HOSPITAL_VISITS("Hospital Visits"),
    MY_NOTES("My Notes"),
    LAB_RESULTS("Lab Results"),
    SPECIAL_AUTHORITY("Special Authority"),
    HEALTH_VISITS("Health Visits"),
    CLINICAL_DOCUMENTS("Clinical Documents");

    companion object {
        private val map = AppFeatureName.values().associateBy(AppFeatureName::value)
        operator fun get(value: String) = map[value]
    }
}

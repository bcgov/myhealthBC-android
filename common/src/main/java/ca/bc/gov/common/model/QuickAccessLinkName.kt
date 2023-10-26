package ca.bc.gov.common.model

enum class QuickAccessLinkName(val value: String) {
    IMMUNIZATIONS("Immunizations"),
    MEDICATIONS("Medications"),
    LAB_RESULTS("Lab Results"),
    COVID_19_TESTS("COVID‑19 Tests"),
    HEALTH_VISITS("Health Visits"),
    MY_NOTES("My Notes"),
    SPECIAL_AUTHORITY("Special Authority"),
    CLINICAL_DOCUMENTS("Clinical Documents"),
    HOSPITAL_VISITS("Hospital Visits"),
    IMAGING_REPORTS("Imaging Reports"),
    ORGAN_DONOR("Organ Donor");

    companion object {
        private val map = QuickAccessLinkName.values().associateBy(QuickAccessLinkName::value)
        operator fun get(value: String) = map[value]
    }
}

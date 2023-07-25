package ca.bc.gov.common.model

enum class AppFeatureName(val value: String) {
    HEALTH_RECORDS("Health records"),
    IMMUNIZATION_SCHEDULES("Immunization schedules"),
    HEALTH_RESOURCES("Health resources"),
    PROOF_OF_VACCINE("Proof of vaccination"),
    SERVICES("Services");

    companion object {
        private val map = AppFeatureName.values().associateBy(AppFeatureName::value)
        operator fun get(value: String) = map[value]
    }
}

package ca.bc.gov.bchealth.ui.home

import ca.bc.gov.bchealth.R

enum class AppFeatureItem(
    val nameStr: String,
    val icon: Int,
    val destination: Int,
    val category: Int,
    val payload: String?,
    val modules: List<String>,
) {

    HEALTH_RECORDS(
        nameStr = "Health records",
        icon = R.drawable.icon_tile_health_record,
        destination = R.id.health_records,
        category = R.string.feature_category_health_record,
        payload = null,
        modules = listOf(),
    ),

    IMMUNIZATION_SCHEDULES(
        nameStr = "Immunization schedules",
        icon = R.drawable.ic_tile_immunization_schedules,
        destination = R.id.immunizationSchedulesFragment,
        category = R.string.feature_category_health_record,
        payload = "Immunization",
        modules = listOf(),
    ),

    HEALTH_RESOURCES(
        nameStr = "Health resources",
        icon = R.drawable.ic_tile_healt_resources,
        destination = R.id.action_homeFragment_to_resources,
        category = R.string.feature_category_health_record,
        payload = null,
        modules = listOf(),
    ),

    PROOF_OF_VACCINE(
        nameStr = "Proof of vaccination",
        icon = R.drawable.ic_tile_proof_of_vaccine,
        destination = R.id.action_homeFragment_to_health_pass,
        category = R.string.feature_category_health_record,
        payload = null,
        modules = listOf(),
    ),

    SERVICES(
        nameStr = "Organ Donor",
        icon = R.drawable.ic_organ_donor,
        destination = R.id.services,
        category = R.string.feature_category_service,
        payload = null,
        modules = listOf(),
    ),

    IMMUNIZATIONS(
        nameStr = "Immunizations",
        icon = R.drawable.ic_health_record_vaccine,
        destination = R.id.health_records,
        category = R.string.feature_category_health_record,
        payload = "Immunization",
        modules = listOf("Immunization"),
    ),

    MEDICATIONS(
        nameStr = "Medications",
        icon = R.drawable.ic_health_record_medication,
        destination = R.id.health_records,
        category = R.string.feature_category_health_record,
        payload = "Medications",
        modules = listOf("Medication"),
    ),

    COVID_TESTS(
        nameStr = "COVID‑19 Tests",
        icon = R.drawable.ic_health_record_covid_test,
        destination = R.id.health_records,
        category = R.string.feature_category_health_record,
        payload = "COVID19Laboratory",
        modules = listOf("Laboratory"),
    ),

    IMAGING_REPORTS(
        nameStr = "Imaging Reports",
        icon = R.drawable.ic_health_record_diagnostic_imaging,
        destination = R.id.health_records,
        category = R.string.feature_category_health_record,
        payload = "ImagingReports",
        modules = listOf("DiagnosticImaging"),
    ),

    HOSPITAL_VISITS(
        nameStr = "Hospital Visits",
        icon = R.drawable.ic_health_record_hospital_visit,
        destination = R.id.health_records,
        category = R.string.feature_category_health_record,
        payload = "HospitalVisit",
        modules = listOf("HospitalVisit"),
    ),

    MY_NOTES(
        nameStr = "My Notes",
        icon = R.drawable.icon_tile_health_record,
        destination = R.id.health_records,
        category = R.string.feature_category_health_record,
        payload = null,
        modules = listOf("Note"),
    ),

    LAB_RESULTS(
        nameStr = "Lab Results",
        icon = R.drawable.ic_lab_test,
        destination = R.id.health_records,
        category = R.string.feature_category_health_record,
        payload = "Laboratory",
        modules = listOf("AllLaboratory"),
    ),

    SPECIAL_AUTHORITY(
        nameStr = "Special Authority",
        icon = R.drawable.ic_health_record_special_authority,
        destination = R.id.health_records,
        category = R.string.feature_category_health_record,
        payload = "SpecialAuthority",
        modules = listOf("MedicationRequest"),
    ),

    HEALTH_VISITS(
        nameStr = "Health Visits",
        icon = R.drawable.ic_health_record_health_visit,
        destination = R.id.health_records,
        category = R.string.feature_category_health_record,
        payload = "HealthVisit",
        modules = listOf("Encounter"),
    ),

    CLINICAL_DOCUMENTS(
        nameStr = "Clinical Documents",
        icon = R.drawable.ic_health_record_clinical_document,
        destination = R.id.health_records,
        category = R.string.feature_category_health_record,
        payload = "ClinicalDocument",
        modules = listOf("ClinicalDocument"),
    );

    companion object {
        private val map = AppFeatureItem.values().associateBy(AppFeatureItem::nameStr)
        operator fun get(nameStr: String) = map[nameStr]
    }
}

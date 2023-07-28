package ca.bc.gov.repository.settings

import ca.bc.gov.common.model.settings.AppFeatureDto
import ca.bc.gov.data.datasource.local.AppFeatureLocalDataSource
import javax.inject.Inject

class AppFeatureRepository @Inject constructor(
    private val appFeatureLocalDataSource: AppFeatureLocalDataSource
) {


    suspend fun loadAppFeatures() {
        listOf(
            // quickLink & non manageable
            AppFeatureDto(
                name = AppFeatureName.IMMUNIZATION_SCHEDULES,
                hasManageableQuickAccessLinks = false,
                showAsQuickAccess = true
            ),

            AppFeatureDto(
                name = AppFeatureName.HEALTH_RESOURCES,
                hasManageableQuickAccessLinks = false,
                showAsQuickAccess = true
            ),

            AppFeatureDto(
                name = AppFeatureName.PROOF_OF_VACCINE,
                hasManageableQuickAccessLinks = false,
                showAsQuickAccess = true
            ),

            // quickLink & manageable
            AppFeatureDto(
                name = AppFeatureName.HEALTH_RECORDS,
                hasManageableQuickAccessLinks = true,
                showAsQuickAccess = true
            ),

            // manageable only
            AppFeatureDto(
                name = AppFeatureName.HEALTH_RECORDS,
                hasManageableQuickAccessLinks = true,
                showAsQuickAccess = false
            ),

            AppFeatureDto(
                name = AppFeatureName.SERVICES,
                hasManageableQuickAccessLinks = true,
                showAsQuickAccess = false
            ),

            AppFeatureDto(
                name = AppFeatureName.IMMUNIZATIONS,
                hasManageableQuickAccessLinks = true,
                showAsQuickAccess = false
            ),

            AppFeatureDto(
                name = AppFeatureName.MEDICATIONS,
                hasManageableQuickAccessLinks = true,
                showAsQuickAccess = false
            ),

            AppFeatureDto(
                name = AppFeatureName.COVID_TESTS,
                hasManageableQuickAccessLinks = true,
                showAsQuickAccess = false
            ),

            AppFeatureDto(
                name = AppFeatureName.IMAGING_REPORTS,
                hasManageableQuickAccessLinks = true,
                showAsQuickAccess = false
            ),

            AppFeatureDto(
                name = AppFeatureName.HOSPITAL_VISITS,
                hasManageableQuickAccessLinks = true,
                showAsQuickAccess = false
            ),

            AppFeatureDto(
                name = AppFeatureName.MY_NOTES,
                hasManageableQuickAccessLinks = true,
                showAsQuickAccess = false
            ),

            AppFeatureDto(
                name = AppFeatureName.LAB_RESULTS,
                hasManageableQuickAccessLinks = true,
                showAsQuickAccess = false
            ),

            AppFeatureDto(
                name = AppFeatureName.SPECIAL_AUTHORITY,
                hasManageableQuickAccessLinks = true,
                showAsQuickAccess = false
            ),

            AppFeatureDto(
                name = AppFeatureName.HEALTH_VISITS,
                hasManageableQuickAccessLinks = true,
                showAsQuickAccess = false
            ),

            AppFeatureDto(
                name = AppFeatureName.CLINICAL_DOCUMENTS,
                hasManageableQuickAccessLinks = true,
                showAsQuickAccess = false
            ),
        ).forEach { insert(it) }
    }

    suspend fun insert(appFeatureDto: AppFeatureDto): Long {
        return appFeatureLocalDataSource.insert(appFeatureDto)
    }

    suspend fun getAppFeaturesWithQuickAccessTiles() = appFeatureLocalDataSource.getAppFeaturesWithQuickAccessTiles()
}

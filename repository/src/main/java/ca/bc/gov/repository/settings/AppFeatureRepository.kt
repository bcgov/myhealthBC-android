package ca.bc.gov.repository.settings

import ca.bc.gov.common.model.AppFeatureName
import ca.bc.gov.common.model.quicklink.QuickLinkDto
import ca.bc.gov.common.model.settings.AppFeatureDto
import ca.bc.gov.data.datasource.local.AppFeatureLocalDataSource
import ca.bc.gov.data.datasource.local.QuickActionTileLocalDataSource
import ca.bc.gov.data.model.mapper.toDto
import javax.inject.Inject

class AppFeatureRepository @Inject constructor(
    private val appFeatureLocalDataSource: AppFeatureLocalDataSource,
    private val quickActionLocalDataSource: QuickActionTileLocalDataSource,
) {

    suspend fun getNonManageableAppFeatures() =
        appFeatureLocalDataSource.getNonManageableAppFeatures().map { it.toDto() }

    suspend fun getManageableAppFeatures() =
        appFeatureLocalDataSource.getManageableAppFeatures().map { it.toDto() }

    suspend fun updateQuickLinks(quickLinks: List<QuickLinkDto>?) {
        if (quickLinks.isNullOrEmpty()) return
        quickActionLocalDataSource.update(quickLinks)
        appFeatureLocalDataSource.updateManageableQuickLinks(quickLinks)
    }

    suspend fun initAppFeatures() {
        listOf(
            // non manageable
            AppFeatureDto(
                name = AppFeatureName.HEALTH_RECORDS,
                hasManageableQuickAccessLinks = false,
                showAsQuickAccess = true
            ),

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

            // manageable
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

    suspend fun getQuickAccessFeatures() =
        appFeatureLocalDataSource.getQuickAccessFeatures()
}

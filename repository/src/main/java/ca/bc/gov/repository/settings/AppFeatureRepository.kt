package ca.bc.gov.repository.settings

import ca.bc.gov.common.const.DATABASE_ERROR
import ca.bc.gov.common.exceptions.MyHealthException
import ca.bc.gov.common.model.AppFeatureName
import ca.bc.gov.common.model.QuickAccessLinkName
import ca.bc.gov.common.model.settings.AppFeatureDto
import ca.bc.gov.common.model.settings.QuickAccessTileDto
import ca.bc.gov.data.datasource.local.AppFeatureLocalDataSource
import ca.bc.gov.data.model.mapper.toDto
import javax.inject.Inject

class AppFeatureRepository @Inject constructor(
    private val appFeatureLocalDataSource: AppFeatureLocalDataSource,
    private val quickAccessTileRepository: QuickAccessTileRepository
) {

    suspend fun insert(appFeatureDto: AppFeatureDto): Long {
        return appFeatureLocalDataSource.insert(appFeatureDto)
    }

    suspend fun getAppFeaturesWithQuickAccessTiles() = appFeatureLocalDataSource.getAppFeaturesWithQuickAccessTiles()

    suspend fun getAppFeature(name: AppFeatureName): AppFeatureDto = appFeatureLocalDataSource.getAppFeature(name)?.toDto() ?: throw MyHealthException(
        DATABASE_ERROR, message = "app feature not found"
    )

    suspend fun initializeAppData() {
        val healthRecord = AppFeatureDto(
            name = AppFeatureName.HEALTH_RECORDS,
            hasManageableQuickAccessLinks = true,
            showAsQuickAccess = true
        )
        val id = insert(healthRecord)

        if (id > 0) {

            val tiles = timelineQuickLinkTiles(id)
            quickAccessTileRepository.insertAll(tiles)
        }

        val immunizationSchedule = AppFeatureDto(
            name = AppFeatureName.IMMUNIZATION_SCHEDULES,
            hasManageableQuickAccessLinks = false,
            showAsQuickAccess = true
        )
        insert(immunizationSchedule)

        val recommendations = AppFeatureDto(
            name = AppFeatureName.RECOMMENDED_IMMUNIZATIONS,
            hasManageableQuickAccessLinks = false,
            showAsQuickAccess = true
        )
        insert(recommendations)

        val healthResources = AppFeatureDto(
            name = AppFeatureName.HEALTH_RESOURCES,
            hasManageableQuickAccessLinks = false,
            showAsQuickAccess = true
        )
        insert(healthResources)

        val proofOfVaccine = AppFeatureDto(
            name = AppFeatureName.PROOF_OF_VACCINE,
            hasManageableQuickAccessLinks = false,
            showAsQuickAccess = true
        )
        insert(proofOfVaccine)

        val services = AppFeatureDto(
            name = AppFeatureName.SERVICES,
            hasManageableQuickAccessLinks = true,
            showAsQuickAccess = false
        )

        val serviceId = insert(services)
        if (serviceId > 0) {
            quickAccessTileRepository.insertAll(serviceQuickLinkTilesItem(serviceId))
        }
    }

    suspend fun addBCCancerQuickLink() {
        try {
            val appFeature = getAppFeature(AppFeatureName.HEALTH_RECORDS)
            val tile = QuickAccessTileDto(
                featureId = appFeature.id,
                tileName = QuickAccessLinkName.BC_CANCER_SCREENING,
                tilePayload = "CancerScreening",
                showAsQuickAccess = false
            )
            quickAccessTileRepository.insert(tile)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun serviceQuickLinkTilesItem(id: Long): List<QuickAccessTileDto> {
        return listOf(
            QuickAccessTileDto(
                featureId = id,
                tileName = QuickAccessLinkName.ORGAN_DONOR,
                tilePayload = "Organ Donor",
                showAsQuickAccess = false
            )
        )
    }

    private fun timelineQuickLinkTiles(id: Long): List<QuickAccessTileDto> {
        return listOf(
            QuickAccessTileDto(
                featureId = id,
                tileName = QuickAccessLinkName.IMMUNIZATIONS,
                tilePayload = "Immunization",
                showAsQuickAccess = false
            ),
            QuickAccessTileDto(
                featureId = id,
                tileName = QuickAccessLinkName.MEDICATIONS,
                tilePayload = "Medications",
                showAsQuickAccess = false
            ),
            QuickAccessTileDto(
                featureId = id,
                tileName = QuickAccessLinkName.LAB_RESULTS,
                tilePayload = "Laboratory",
                showAsQuickAccess = false
            ),
            QuickAccessTileDto(
                featureId = id,
                tileName = QuickAccessLinkName.COVID_19_TESTS,
                tilePayload = "COVID19Laboratory",
                showAsQuickAccess = false
            ),
            QuickAccessTileDto(
                featureId = id,
                tileName = QuickAccessLinkName.HEALTH_VISITS,
                tilePayload = "HealthVisit",
                showAsQuickAccess = false
            ),
            QuickAccessTileDto(
                featureId = id,
                tileName = QuickAccessLinkName.SPECIAL_AUTHORITY,
                tilePayload = "SpecialAuthority",
                showAsQuickAccess = false
            ),
            QuickAccessTileDto(
                featureId = id,
                tileName = QuickAccessLinkName.HOSPITAL_VISITS,
                tilePayload = "HospitalVisit",
                showAsQuickAccess = false
            ),
            QuickAccessTileDto(
                featureId = id,
                tileName = QuickAccessLinkName.CLINICAL_DOCUMENTS,
                tilePayload = "ClinicalDocument",
                showAsQuickAccess = false
            ),
            QuickAccessTileDto(
                featureId = id,
                tileName = QuickAccessLinkName.IMAGING_REPORTS,
                tilePayload = "ImagingReports",
                showAsQuickAccess = false
            ),
            QuickAccessTileDto(
                featureId = id,
                tileName = QuickAccessLinkName.BC_CANCER_SCREENING,
                tilePayload = "CancerScreening",
                showAsQuickAccess = false
            )

        )
    }
}

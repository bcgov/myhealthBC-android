package ca.bc.gov.repository.settings

import ca.bc.gov.common.const.DATABASE_ERROR
import ca.bc.gov.common.exceptions.MyHealthException
import ca.bc.gov.common.model.AppFeatureName
import ca.bc.gov.common.model.settings.AppFeatureDto
import ca.bc.gov.data.datasource.local.AppFeatureLocalDataSource
import ca.bc.gov.data.model.mapper.toDto
import javax.inject.Inject

class AppFeatureRepository @Inject constructor(
    private val appFeatureLocalDataSource: AppFeatureLocalDataSource
) {

    suspend fun insert(appFeatureDto: AppFeatureDto): Long {
        return appFeatureLocalDataSource.insert(appFeatureDto)
    }

    suspend fun getAppFeaturesWithQuickAccessTiles() = appFeatureLocalDataSource.getAppFeaturesWithQuickAccessTiles()

    suspend fun getAppFeature(name: AppFeatureName): AppFeatureDto = appFeatureLocalDataSource.getAppFeature(name)?.toDto() ?: throw MyHealthException(
        DATABASE_ERROR, message = "app feature not found"
    )
}

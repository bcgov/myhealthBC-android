package ca.bc.gov.repository.immunization

import ca.bc.gov.common.model.immunization.ImmunizationRecommendationsDto
import ca.bc.gov.data.datasource.local.ImmunizationRecommendationLocalDataSource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ImmunizationRecommendationRepository @Inject constructor(
    private val localDataSource: ImmunizationRecommendationLocalDataSource
) {

    fun getAllRecommendations(): Flow<List<ImmunizationRecommendationsDto>> =
        localDataSource.getAllRecommendations()

    suspend fun insert(immunizationRecommendation: ImmunizationRecommendationsDto): Long =
        localDataSource.insert(immunizationRecommendation)

    suspend fun insert(immunizationRecommendations: List<ImmunizationRecommendationsDto>): List<Long> =
        localDataSource.insert(immunizationRecommendations)

    suspend fun delete(patientId: Long): Int = localDataSource.delete(patientId)
}

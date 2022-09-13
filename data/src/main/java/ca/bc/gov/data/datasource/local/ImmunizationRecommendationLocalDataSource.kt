package ca.bc.gov.data.datasource.local

import ca.bc.gov.common.model.immunization.ImmunizationRecommendationsDto
import ca.bc.gov.data.datasource.local.dao.ImmunizationRecommendationDao
import ca.bc.gov.data.model.mapper.toEntity
import javax.inject.Inject

class ImmunizationRecommendationLocalDataSource @Inject constructor(
    private val dao: ImmunizationRecommendationDao
) {

    suspend fun insert(immunizationRecommendation: ImmunizationRecommendationsDto): Long {
        return dao.insert(immunizationRecommendation.toEntity())
    }

    suspend fun insert(immunizationRecommendation: List<ImmunizationRecommendationsDto>): List<Long> =
        dao.insert(immunizationRecommendation.map { it.toEntity() })

    suspend fun delete(patientId: Long): Int = dao.delete(patientId)

}

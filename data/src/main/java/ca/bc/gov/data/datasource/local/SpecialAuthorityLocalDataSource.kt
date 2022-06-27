package ca.bc.gov.data.datasource.local

import ca.bc.gov.common.model.specialauthority.SpecialAuthorityDto
import ca.bc.gov.data.datasource.local.dao.SpecialAuthorityDao
import ca.bc.gov.data.model.mapper.toDto
import ca.bc.gov.data.model.mapper.toEntity
import javax.inject.Inject

/*
* Created by amit_metri on 27,June,2022
*/
class SpecialAuthorityLocalDataSource @Inject constructor(
    private val specialAuthorityDao: SpecialAuthorityDao
) {
    suspend fun deleteSpecialAuthorities(patientId: Long) = specialAuthorityDao.delete(patientId)

    suspend fun insert(specialAuthorities: List<SpecialAuthorityDto>) = specialAuthorityDao.insert(
        specialAuthorities.map {
            it.toEntity()
        }
    )

    suspend fun getSpecialAuthorityDetails(id: Long) =
        specialAuthorityDao.getSpecialAuthorityDetails(id)?.toDto()
}

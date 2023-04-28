package ca.bc.gov.data.datasource.local

import ca.bc.gov.common.model.services.OrganDonorDto
import ca.bc.gov.data.datasource.local.dao.OrganDonorDao
import ca.bc.gov.data.model.mapper.toDto
import ca.bc.gov.data.model.mapper.toEntity
import javax.inject.Inject

/**
 * @author Pinakin Kansara
 */
class OrganDonorLocalDataSource @Inject constructor(
    private val organDonorDao: OrganDonorDao
) {

    suspend fun insert(organDonorDto: OrganDonorDto): Long = organDonorDao.insert(organDonorDto.toEntity())

    suspend fun findOrganDonorById(patientId: Long): OrganDonorDto? =
        organDonorDao.findOrganDonorById(patientId)?.toDto()

    suspend fun delete(patientId: Long): Int = organDonorDao.delete(patientId)

    suspend fun update(organDonorDto: OrganDonorDto) = organDonorDao.update(organDonorDto.toEntity())
}

package ca.bc.gov.data.datasource.local

import ca.bc.gov.common.model.services.OrganDonationDto
import ca.bc.gov.data.datasource.local.dao.OrganDonationDao
import ca.bc.gov.data.model.mapper.toDto
import ca.bc.gov.data.model.mapper.toEntity
import javax.inject.Inject

/**
 * @author Pinakin Kansara
 */
class OrganDonationLocalDataSource @Inject constructor(
    private val organDonationDao: OrganDonationDao
) {

    suspend fun insert(organDonationDto: OrganDonationDto): Long = organDonationDao.insert(organDonationDto.toEntity())

    suspend fun findOrganDonationById(patientId: Long): OrganDonationDto? =
        organDonationDao.findOrganDonationById(patientId)?.toDto()

    suspend fun delete(patientId: Long): Int = organDonationDao.delete(patientId)

    suspend fun update(organDonationDto: OrganDonationDto) = organDonationDao.update(organDonationDto.toEntity())
}

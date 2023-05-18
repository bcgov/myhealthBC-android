package ca.bc.gov.repository.services

import ca.bc.gov.common.const.DATABASE_ERROR
import ca.bc.gov.common.exceptions.MyHealthException
import ca.bc.gov.common.model.services.OrganDonorDto
import ca.bc.gov.data.datasource.local.OrganDonorLocalDataSource
import javax.inject.Inject

/**
 * @author Pinakin Kansara
 */
class OrganDonorRepository @Inject constructor(
    private val organDonorLocalDataSource: OrganDonorLocalDataSource
) {

    suspend fun insert(organDonor: OrganDonorDto) =
        organDonorLocalDataSource.insert(organDonor)

    suspend fun delete(patientId: Long) = organDonorLocalDataSource.delete(patientId)

    suspend fun findOrganDonationStatusById(patientId: Long) =
        organDonorLocalDataSource.findOrganDonorById(patientId) ?: throw MyHealthException(
            DATABASE_ERROR, "No record found for patient id=  $patientId"
        )

    suspend fun update(organDonor: OrganDonorDto) = organDonorLocalDataSource.update(organDonor)
}

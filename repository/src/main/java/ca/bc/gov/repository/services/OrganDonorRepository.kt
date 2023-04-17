package ca.bc.gov.repository.services

import ca.bc.gov.common.const.DATABASE_ERROR
import ca.bc.gov.common.exceptions.MyHealthException
import ca.bc.gov.common.model.services.OrganDonationDto
import ca.bc.gov.data.datasource.local.OrganDonationLocalDataSource
import ca.bc.gov.data.datasource.remote.OrganDonorRemoteDataSource
import javax.inject.Inject

/**
 * @author Pinakin Kansara
 */
class OrganDonorRepository @Inject constructor(
    private val organDonorRemoteDataSource: OrganDonorRemoteDataSource,
    private val organDonationLocalDataSource: OrganDonationLocalDataSource
) {

    suspend fun insert(organDonor: OrganDonationDto) =
        organDonationLocalDataSource.insert(organDonor)

    suspend fun delete(patientId: Long) = organDonationLocalDataSource.delete(patientId)

    suspend fun fetchOrganDonationStatus(hdid: String, token: String) =
        organDonorRemoteDataSource.getOrganDonorData(hdid, token)

    suspend fun fetchPatientFile(hdid: String, token: String, fileId: String) =
        organDonorRemoteDataSource.getPatientFile(hdid, token, fileId)

    suspend fun findOrganDonationStatusById(patientId: Long) =
        organDonationLocalDataSource.findOrganDonationById(patientId) ?: throw MyHealthException(
            DATABASE_ERROR, "No record found for patient id=  $patientId"
        )
}

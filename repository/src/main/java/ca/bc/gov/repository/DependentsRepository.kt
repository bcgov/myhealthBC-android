package ca.bc.gov.repository

import ca.bc.gov.data.datasource.remote.DependentsRemoteDataSource
import ca.bc.gov.repository.bcsc.BcscAuthRepo
import javax.inject.Inject

class DependentsRepository @Inject constructor(
    private val dependentsRemoteDataSource: DependentsRemoteDataSource,
    private val bcscAuthRepo: BcscAuthRepo,
) {

    suspend fun addDependent(
        firstName: String,
        lastName: String,
        dateOfBirth: String,
        phn: String,
    ) {
        val (token, hdid) = bcscAuthRepo.getAuthParameters()

        dependentsRemoteDataSource.addDependent(
            hdid,
            firstName,
            lastName,
            dateOfBirth,
            phn,
            token,
        )
    }
}
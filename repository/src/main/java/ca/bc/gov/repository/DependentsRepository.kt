package ca.bc.gov.repository

import ca.bc.gov.data.datasource.remote.DependentsLocalDataSource
import ca.bc.gov.data.datasource.remote.DependentsRemoteDataSource
import ca.bc.gov.repository.bcsc.BcscAuthRepo
import javax.inject.Inject

class DependentsRepository @Inject constructor(
    private val remoteDataSource: DependentsRemoteDataSource,
    private val localDataSource: DependentsLocalDataSource,
    private val bcscAuthRepo: BcscAuthRepo,
) {

    suspend fun getAllDependents() {
        localDataSource.getAllDependents()
    }

    suspend fun fetchAllDependents() {
        val (token, hdid) = bcscAuthRepo.getAuthParameters()
        remoteDataSource.fetchAllDependents(hdid, token)
    }

    suspend fun addDependent(
        firstName: String,
        lastName: String,
        dateOfBirth: String,
        phn: String,
    ) {
        val (token, hdid) = bcscAuthRepo.getAuthParameters()

        remoteDataSource.addDependent(
            hdid,
            firstName,
            lastName,
            dateOfBirth,
            phn,
            token,
        )
    }
}

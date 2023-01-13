package ca.bc.gov.repository.hospitalvisit

import ca.bc.gov.data.datasource.local.HospitalVisitLocalDataSource
import ca.bc.gov.data.datasource.remote.HospitalVisitRemoteDataSource
import javax.inject.Inject

class HospitalVisitRepository @Inject constructor(
    private val localDataSource: HospitalVisitLocalDataSource,
    private val remoteDataSource: HospitalVisitRemoteDataSource
) {

    suspend fun getHospitalVisits(token: String, hdid: String) =
        remoteDataSource.getHospitalVisit(token, hdid)
}

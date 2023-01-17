package ca.bc.gov.repository.hospitalvisit

import ca.bc.gov.common.const.DATABASE_ERROR
import ca.bc.gov.common.exceptions.MyHealthException
import ca.bc.gov.common.model.hospitalvisits.HospitalVisitDto
import ca.bc.gov.data.datasource.local.HospitalVisitLocalDataSource
import ca.bc.gov.data.datasource.remote.HospitalVisitRemoteDataSource
import ca.bc.gov.data.model.mapper.toDto
import javax.inject.Inject

class HospitalVisitRepository @Inject constructor(
    private val localDataSource: HospitalVisitLocalDataSource,
    private val remoteDataSource: HospitalVisitRemoteDataSource
) {
    suspend fun deleteHospitalVisits(patientId: Long) =
        localDataSource.deleteHospitalVisits(patientId)

    suspend fun insertHospitalVisits(list: List<HospitalVisitDto>) =
        localDataSource.insertHospitalVisits(list)

    suspend fun getHospitalVisits(token: String, hdid: String) =
        remoteDataSource.getHospitalVisit(token, hdid)

    suspend fun getHospitalVisit(hospitalVisitId: Long) =
        localDataSource.getHospitalVisit(hospitalVisitId)?.toDto()
            ?: throw MyHealthException(
                DATABASE_ERROR, "No record found for hospitalVisitId = $hospitalVisitId"
            )
}

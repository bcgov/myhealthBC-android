package ca.bc.gov.repository

import ca.bc.gov.common.model.dependents.DependentDto
import ca.bc.gov.data.datasource.local.DependentsLocalDataSource
import ca.bc.gov.data.datasource.local.PatientLocalDataSource
import ca.bc.gov.data.datasource.remote.DependentsRemoteDataSource
import ca.bc.gov.data.model.mapper.toDto
import ca.bc.gov.data.model.mapper.toEntity
import ca.bc.gov.repository.bcsc.BcscAuthRepo
import ca.bc.gov.repository.extensions.mapFlowContent
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class DependentsRepository @Inject constructor(
    private val remoteDataSource: DependentsRemoteDataSource,
    private val localDataSource: DependentsLocalDataSource,
    private val patientLocalDataSource: PatientLocalDataSource,
    private val bcscAuthRepo: BcscAuthRepo,
) {

    fun getAllDependents(): Flow<List<DependentDto>> =
        localDataSource.getAllDependents().mapFlowContent {
            it.toDto()
        }

    suspend fun fetchAllDependents(token: String, hdid: String): List<DependentDto> {
        return remoteDataSource.fetchAllDependents(hdid, token).map {
            it.dependentInformation.toDto()
        }
    }

    suspend fun storeDependents(list: List<DependentDto>, patientId: Long) {
        localDataSource.clearTable()
        localDataSource.insertDependents(list.map { it.toEntity(patientId) })
    }

    suspend fun registerDependent(
        firstName: String,
        lastName: String,
        dateOfBirth: String,
        phn: String,
    ) {
        val (token, hdid) = bcscAuthRepo.getAuthParameters()

        val patientId = patientLocalDataSource.getAuthenticatedPatientId()

        val response = remoteDataSource.addDependent(
            hdid,
            firstName,
            lastName,
            dateOfBirth,
            phn,
            token,
        )

        localDataSource.insertDependents(listOf(response.toDto().toEntity(patientId)))
    }
    }
}

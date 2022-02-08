package ca.bc.gov.repository

import ca.bc.gov.data.MedicationRemoteDataSource
import ca.bc.gov.repository.bcsc.BcscAuthRepo
import javax.inject.Inject

/*
* Created by amit_metri on 08,February,2022
*/
class MedicationRepository @Inject constructor(
    private val medicationRemoteDataSource: MedicationRemoteDataSource,
    private val bcscAuthRepo: BcscAuthRepo
) {
    suspend fun fetchMedicationStatement() {
        val authParameters = bcscAuthRepo.getAuthParameters()
        val response = medicationRemoteDataSource.getMedicationStatement(
            authParameters.first, authParameters.second
        )
        // TODO: 08/02/22  Insertion of medication statement records into DB
    }
}

package ca.bc.gov.repository.testrecord

import ca.bc.gov.common.const.DATABASE_ERROR
import ca.bc.gov.common.exceptions.MyHealthException
import ca.bc.gov.common.model.test.CovidOrderDto
import ca.bc.gov.common.model.test.CovidOrderWithCovidTestAndPatientDto
import ca.bc.gov.data.datasource.local.CovidOrderLocalDataSource
import ca.bc.gov.data.datasource.remote.LaboratoryRemoteDataSource
import ca.bc.gov.repository.bcsc.BcscAuthRepo
import javax.inject.Inject

/**
 * @author Pinakin Kansara
 */
class CovidOrderRepository @Inject constructor(
    private val laboratoryRemoteDataSource: LaboratoryRemoteDataSource,
    private val covidOrderLocalDataSource: CovidOrderLocalDataSource,
    private val bcscAuthRepo: BcscAuthRepo
) {

    suspend fun insert(covidOrder: CovidOrderDto): Long =
        covidOrderLocalDataSource.insert(covidOrder)

    suspend fun insert(covidOrders: List<CovidOrderDto>): List<Long> =
        covidOrderLocalDataSource.insert(covidOrders)

    suspend fun findByCovidOrderId(covidOrderId: String): CovidOrderWithCovidTestAndPatientDto =
        covidOrderLocalDataSource.findCovidOrderById(covidOrderId)
            ?: throw MyHealthException(
                DATABASE_ERROR, "No record found for covidOrder id=  $covidOrderId"
            )

    suspend fun deleteByPatientId(patientId: Long): Int = covidOrderLocalDataSource.deleteByPatientId(patientId)

    suspend fun delete(id: String): Int = covidOrderLocalDataSource.delete(id)

    suspend fun fetchCovidOrders(token: String, hdid: String) =
        laboratoryRemoteDataSource.getCovidTests(token, hdid)

    suspend fun fetchCovidTestPdf(
        reportId: String,
        isCovid19: Boolean
    ): String? {
        val authParameters = bcscAuthRepo.getAuthParameters()
        return laboratoryRemoteDataSource.getLabTestInPdf(
            authParameters.first,
            authParameters.second,
            reportId,
            isCovid19 // true for Covid-19 tests
        ).resourcePayload?.data
    }
}

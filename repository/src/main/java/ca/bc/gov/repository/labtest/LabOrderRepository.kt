package ca.bc.gov.repository.labtest

import ca.bc.gov.common.const.DATABASE_ERROR
import ca.bc.gov.common.exceptions.MyHealthException
import ca.bc.gov.common.model.labtest.LabOrderDto
import ca.bc.gov.common.model.labtest.LabOrderWithLabTestDto
import ca.bc.gov.common.model.labtest.LabOrderWithLabTestsAndPatientDto
import ca.bc.gov.data.datasource.local.LabOrderLocalDataSource
import ca.bc.gov.data.datasource.remote.LaboratoryRemoteDataSource
import ca.bc.gov.repository.bcsc.BcscAuthRepo
import javax.inject.Inject

/**
 * @author Pinakin Kansara
 */
class LabOrderRepository @Inject constructor(
    private val laboratoryRemoteDataSource: LaboratoryRemoteDataSource,
    private val labOrderLocalDataSource: LabOrderLocalDataSource,
    private val bcscAuthRepo: BcscAuthRepo
) {

    suspend fun insert(labOrder: LabOrderDto): Long =
        labOrderLocalDataSource.insert(labOrder)

    suspend fun insert(labOrders: List<LabOrderDto>): List<Long> =
        labOrderLocalDataSource.insert(labOrders)

    suspend fun findByLabOrderId(labOrderId: Long): LabOrderWithLabTestsAndPatientDto =
        labOrderLocalDataSource.findByLabOrderId(labOrderId)
            ?: throw MyHealthException(
                DATABASE_ERROR, "No record found for labOrder id=  $labOrderId"
            )

    suspend fun delete(patientId: Long): Int = labOrderLocalDataSource.delete(patientId)

    suspend fun fetchLabOrders(token: String, hdid: String): List<LabOrderWithLabTestDto> =
        laboratoryRemoteDataSource.getLabTests(token, hdid)

    suspend fun fetchLabTestPdf(
        reportId: String,
        isCovid19: Boolean
    ): String? {
        val authParameters = bcscAuthRepo.getAuthParameters()
        return laboratoryRemoteDataSource.getLabTestInPdf(
            authParameters.first,
            authParameters.second,
            reportId,
            isCovid19
        ).resourcePayload?.data
    }
}

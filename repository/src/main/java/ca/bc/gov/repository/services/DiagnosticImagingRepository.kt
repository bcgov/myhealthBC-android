package ca.bc.gov.repository.services

import ca.bc.gov.common.const.DATABASE_ERROR
import ca.bc.gov.common.exceptions.MyHealthException
import ca.bc.gov.common.model.services.DiagnosticImagingDataDto
import ca.bc.gov.data.datasource.local.DiagnosticImagingLocalDataSource
import javax.inject.Inject

/**
 * @author Pinakin Kansara
 */
class DiagnosticImagingRepository @Inject constructor(
    private val localDataSource: DiagnosticImagingLocalDataSource
) {

    suspend fun insert(diagnosticImagingDataDtoList: List<DiagnosticImagingDataDto>) =
        localDataSource.insert(diagnosticImagingDataDtoList)

    suspend fun getDiagnosticImagingDataDetails(id: Long): DiagnosticImagingDataDto =
        localDataSource.getDiagnosticImagingDataDetails(id) ?: throw MyHealthException(
            DATABASE_ERROR, "No record found for diagnostic imaging id=  $id"
        )
}

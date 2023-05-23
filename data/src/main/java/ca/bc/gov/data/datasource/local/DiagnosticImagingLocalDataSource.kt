package ca.bc.gov.data.datasource.local

import ca.bc.gov.common.model.services.DiagnosticImagingDataDto
import ca.bc.gov.data.datasource.local.dao.DiagnosticImagingDataDao
import ca.bc.gov.data.model.mapper.toDto
import ca.bc.gov.data.model.mapper.toEntity
import javax.inject.Inject

/**
 * @author Pinakin Kansara
 */
class DiagnosticImagingLocalDataSource @Inject constructor(
    private val diagnosticImagingDataDao: DiagnosticImagingDataDao
) {

    suspend fun insert(diagnosticImagingDataList: List<DiagnosticImagingDataDto>) = diagnosticImagingDataDao.insert(diagnosticImagingDataList.map { it.toEntity() })

    suspend fun getDiagnosticImagingDataDetails(id: Long): DiagnosticImagingDataDto? = diagnosticImagingDataDao.getDiagnosticImagingDataDetails(id)?.toDto()
}

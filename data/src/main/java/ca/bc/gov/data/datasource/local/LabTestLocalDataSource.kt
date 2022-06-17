package ca.bc.gov.data.datasource.local

import ca.bc.gov.common.model.labtest.LabTestDto
import ca.bc.gov.data.datasource.local.dao.LabTestDao
import ca.bc.gov.data.model.mapper.toEntity
import javax.inject.Inject

/**
 * @author Pinakin Kansara
 */
class LabTestLocalDataSource @Inject constructor(
    private val labTestDao: LabTestDao
) {

    suspend fun insert(labTest: LabTestDto): Long = labTestDao.insert(labTest.toEntity())

    suspend fun insert(labTests: List<LabTestDto>): List<Long> =
        labTestDao.insert(labTests.map { it.toEntity() })
}

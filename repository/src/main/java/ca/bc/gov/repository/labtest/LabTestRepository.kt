package ca.bc.gov.repository.labtest

import ca.bc.gov.common.model.labtest.LabTestDto
import ca.bc.gov.data.datasource.local.LabTestLocalDataSource
import javax.inject.Inject

/**
 * @author Pinakin Kansara
 */
class LabTestRepository @Inject constructor(
    private val labTestLocalDataSource: LabTestLocalDataSource
) {

    suspend fun insert(labTest: LabTestDto): Long = labTestLocalDataSource.insert(labTest)

    suspend fun insert(labTests: List<LabTestDto>): List<Long> =
        labTestLocalDataSource.insert(labTests)
}

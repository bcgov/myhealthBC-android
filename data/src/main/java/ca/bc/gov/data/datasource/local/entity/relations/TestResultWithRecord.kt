package ca.bc.gov.data.datasource.local.entity.relations

import androidx.room.Embedded
import androidx.room.Relation
import ca.bc.gov.data.datasource.local.entity.covid.test.TestRecordEntity
import ca.bc.gov.data.datasource.local.entity.covid.test.TestResultEntity

/**
 * @author Pinakin Kansara
 */
data class TestResultWithRecord(
    @Embedded
    val testResult: TestResultEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "test_result_id"
    )
    val testRecords: List<TestRecordEntity>
)

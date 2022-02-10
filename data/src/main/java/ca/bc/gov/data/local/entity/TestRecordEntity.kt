package ca.bc.gov.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import ca.bc.gov.common.model.DataSource
import java.time.Instant

/**
 * @author Pinakin Kansara
 */
@Entity(
    tableName = "test_record",
    foreignKeys = [
        ForeignKey(
            entity = TestResultEntity::class,
            parentColumns = ["id"],
            childColumns = ["test_result_id"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        )
    ]
)
data class TestRecordEntity(
    @PrimaryKey
    val id: String,
    @ColumnInfo(name = "test_result_id")
    var testResultId: Long,
    @ColumnInfo(name = "lab_Name")
    val labName: String,
    @ColumnInfo(name = "collection_time")
    val collectionDateTime: Instant,
    @ColumnInfo(name = "result_time")
    val resultDateTime: Instant,
    @ColumnInfo(name = "test_name")
    val testName: String,
    @ColumnInfo(name = "test_type")
    val testType: String?,
    @ColumnInfo(name = "test_status")
    val testStatus: String,
    @ColumnInfo(name = "test_outcome")
    val testOutcome: String,
    @ColumnInfo(name = "result_title")
    val resultTitle: String,
    @ColumnInfo(name = "result_desc")
    val resultDescription: String,
    @ColumnInfo(name = "result_link")
    val resultLink: String,
    @ColumnInfo(name = "data_source")
    var dataSource: DataSource = DataSource.PUBLIC_API
)

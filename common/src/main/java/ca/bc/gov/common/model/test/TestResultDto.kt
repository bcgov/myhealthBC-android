package ca.bc.gov.common.model.test

import ca.bc.gov.common.model.DataSource
import java.time.Instant

/**
 * @author Pinakin Kansara
 */
data class TestResultDto(
    var id: Long = 0,
    var patientId: Long = 0,
    val collectionDate: Instant,
    var dataSource: DataSource = DataSource.PUBLIC_API
)

package ca.bc.gov.repository

import ca.bc.gov.data.datasource.PatientLocalDataSource
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * @author Pinakin Kansara
 */
class PatientHealthRecordsRepository @Inject constructor(
    private val localDataSource: PatientLocalDataSource
) {

    val patientHealthRecords =
        localDataSource.patientWithRecordCount.map { patientHealthRecords ->
            patientHealthRecords.filter { record ->
                (record.vaccineRecordCount + record.testResultCount) > 0
            }
        }
}


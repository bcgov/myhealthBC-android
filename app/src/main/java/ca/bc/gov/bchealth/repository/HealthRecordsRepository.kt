package ca.bc.gov.bchealth.repository

import ca.bc.gov.bchealth.data.local.entity.CovidTestResult
import ca.bc.gov.bchealth.datasource.LocalDataSource
import ca.bc.gov.bchealth.model.HealthCardDto
import ca.bc.gov.bchealth.model.healthrecords.HealthRecord
import ca.bc.gov.bchealth.model.healthrecords.VaccineData
import ca.bc.gov.bchealth.utils.Response
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import java.sql.Date
import javax.inject.Inject

/*
* Created by amit_metri on 26,November,2021
*/
class HealthRecordsRepository @Inject constructor(
    private val cardRepository: CardRepository,
    private val dataSource: LocalDataSource
) {

    /*
    * Used to manage Success, Error and Loading status in the UI
    * */
    private val responseMutableSharedFlow = MutableSharedFlow<Response<String>>()
    val responseSharedFlow: SharedFlow<Response<String>>
        get() = responseMutableSharedFlow.asSharedFlow()

    fun prepareHealthRecords(healthCardDto: HealthCardDto): HealthRecord {
        val vaccineDataList: MutableList<VaccineData> = mutableListOf()
        healthCardDto.immunizationEntries?.forEachIndexed { index, entry ->

            vaccineDataList.add(
                VaccineData(
                    healthCardDto.name,
                    (index + 1).toString(),
                    entry.resource.occurrenceDateTime,
                    "",
                    "",
                    entry.resource.performer?.last()?.actor?.display,
                    entry.resource.lotNumber
                )
            )
        }
        return HealthRecord(
            healthCardDto.name,
            healthCardDto.status,
            healthCardDto.issueDate,
            vaccineDataList,
            mutableListOf()
        )
    }

    suspend fun getCovidTestResult(phn: String, dob: String, dot: Any) {

        // TODO: 26/11/21 Network call to be implemented

        saveCovidTestResult()
    }

    private suspend fun saveCovidTestResult() {
        responseMutableSharedFlow.emit(Response.Loading())

        dataSource.insertCovidTestResult(
            CovidTestResult(
                kotlin.random.Random.toString(),
                "Amit",
                "Freshworks lab",
                Date.valueOf("2021-10-10"),
                Date.valueOf("2021-10-11"),
                "Covid Test",
                "Test Type",
                "COMPLETED",
                "POSITIVE",
                "Tested Positive",
                "Tested positive description",
                "link",
                ""
            )

        )

        responseMutableSharedFlow.emit(Response.Success())
    }

    /*
    * Covid test results fetched from DB
    * */
    val covidTestResults: Flow<List<CovidTestResult>> = dataSource.getCovidTestResults()
}
package ca.bc.gov.bchealth.repository

import ca.bc.gov.bchealth.data.local.entity.CovidTestResult
import ca.bc.gov.bchealth.datasource.LocalDataSource
import ca.bc.gov.bchealth.model.healthrecords.HealthRecord
import ca.bc.gov.bchealth.model.healthrecords.VaccineData
import ca.bc.gov.bchealth.utils.Response
import ca.bc.gov.bchealth.utils.SHCDecoder
import ca.bc.gov.bchealth.utils.getDateTime
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.collect
import java.sql.Date
import javax.inject.Inject

/*
* Created by amit_metri on 26,November,2021
*/
class HealthRecordsRepository @Inject constructor(
    private val dataSource: LocalDataSource,
    private val shcDecoder: SHCDecoder,
) {

    /*
    * Used to manage Success, Error and Loading status in the UI
    * */
    private val responseMutableSharedFlow = MutableSharedFlow<Response<String>>()
    val responseSharedFlow: SharedFlow<Response<String>>
        get() = responseMutableSharedFlow.asSharedFlow()

    /*
    * Used as an observable for healthRecords
    * */
    private val healthRecordsMutableSharedFlow = MutableSharedFlow<List<HealthRecord>>()
    val healthRecordsSharedFlow: SharedFlow<List<HealthRecord>>
        get() = healthRecordsMutableSharedFlow.asSharedFlow()

    /*
    * Prepare health records from Health Passes and Covid test results data
    * */
    suspend fun prepareHealthRecords() {

        responseMutableSharedFlow.emit(Response.Loading())

        val healthRecordList: MutableList<HealthRecord> = mutableListOf()

        /*
        * Collect covid test results from DB
        * */
        dataSource.getCovidTestResults().collect { covidTestResults ->

            /*
            * Collect health passes from DB
            * */
            dataSource.getCards().collect { healthPasses ->

                healthPasses.forEach { healthPass ->

                    try {
                        val data = shcDecoder.getImmunizationStatus(healthPass.uri)

                        /*
                        * Prepare vaccination record a member
                        * */
                        val vaccineDataList: MutableList<VaccineData> = mutableListOf()
                        data.immunizationEntries?.forEachIndexed { index, entry ->
                            vaccineDataList.add(
                                VaccineData(
                                    data.name,
                                    (index + 1).toString(),
                                    entry.resource.occurrenceDateTime,
                                    "", // TODO: 29/11/21 Data to be prepared later
                                    "",
                                    entry.resource.performer?.last()?.actor?.display,
                                    entry.resource.lotNumber
                                )
                            )
                        }


                        /*
                        * Add common data, vaccination data and covid test results data to health record of a member
                        * */
                        healthRecordList.add(
                            HealthRecord(
                                data.name,
                                data.status,
                                data.issueDate.getDateTime(),
                                vaccineDataList,
                                covidTestResults.filter {
                                    it.patientDisplayName.lowercase() == data.name.lowercase()
                                }
                            )
                        )
                    } catch (e: Exception) {

                    }
                }


                /*
                * There is possibility that a member may not get vaccinated but can have covid test results.
                * Below method prepares the health records for such members
                * */
                covidTestResults.forEach { covidTestResult ->
                    var isHealthRecordAlreadyPresent = false
                    healthRecordList.forEach innerLoop@{ healthRecord ->
                        if (covidTestResult.patientDisplayName.lowercase()
                            == healthRecord.name.lowercase()
                        ) {
                            isHealthRecordAlreadyPresent = true
                            return@innerLoop
                        }
                    }

                    if (!isHealthRecordAlreadyPresent) {
                        healthRecordList.add(HealthRecord(
                            covidTestResult.patientDisplayName,
                            null,
                            "",
                            mutableListOf(),
                            covidTestResults.filter {
                                covidTestResult.patientDisplayName.lowercase() ==
                                        it.patientDisplayName.lowercase()
                            }
                        ))
                    }
                }

                //Emit health records
                healthRecordsMutableSharedFlow.emit(healthRecordList)

                responseMutableSharedFlow.emit(Response.Success())
            }
        }
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
                "AMIT METRI",
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
    //val covidTestResults: Flow<List<CovidTestResult>> = dataSource.getCovidTestResults()
}
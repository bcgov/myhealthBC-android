package ca.bc.gov.bchealth.repository

import ca.bc.gov.bchealth.data.local.entity.CovidTestResult
import ca.bc.gov.bchealth.data.local.entity.HealthCard
import ca.bc.gov.bchealth.datasource.LocalDataSource
import ca.bc.gov.bchealth.model.ImmunizationRecord
import ca.bc.gov.bchealth.model.healthrecords.HealthRecord
import ca.bc.gov.bchealth.model.healthrecords.VaccineData
import ca.bc.gov.bchealth.utils.Response
import ca.bc.gov.bchealth.utils.SHCDecoder
import ca.bc.gov.bchealth.utils.getDateTime
import java.sql.Date
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.collect

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
                        * Add common data, vaccination data and covid test results data to health record of a member
                        * */
                        healthRecordList.add(
                            HealthRecord(
                                data.name,
                                data.status,
                                data.issueDate.getDateTime(),
                                getIndividualVaccinationData(data),
                                covidTestResults.filter {
                                    it.patientDisplayName.lowercase() == data.name.lowercase()
                                }
                            )
                        )
                    } catch (e: Exception) {
                        e.printStackTrace()
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
                        healthRecordList.add(
                            HealthRecord(
                                covidTestResult.patientDisplayName,
                                null,
                                "",
                                mutableListOf(),
                                covidTestResults.filter {
                                    covidTestResult.patientDisplayName.lowercase() ==
                                        it.patientDisplayName.lowercase()
                                }
                            )
                        )
                    }
                }

                // Emit health records
                healthRecordsMutableSharedFlow.emit(healthRecordList)

                responseMutableSharedFlow.emit(Response.Success())
            }
        }
    }

    val vaccineInfo: HashMap<String, String> = mapOf(
        "28581000087106" to "PFIZER-BIONTECH COMIRNATY COVID-19",
        "28571000087109" to "MODERNA SPIKEVAX",
        "28761000087108" to "ASTRAZENECA VAXZEVRIA",
        "28961000087105" to "COVISHIELD",
        "28951000087107" to "JANSSEN (JOHNSON & JOHNSON)",
        "29171000087106" to "NOVAVAX",
        "31431000087100" to "CANSINOBIO",
        "31341000087103" to "SPUTNIK",
        "31311000087104" to "SINOVAC-CORONAVAC ",
        "31301000087101" to "SINOPHARM",
        "NON-WHO" to "UNSPECIFIED COVID-19 VACCINE"
    ) as HashMap<String, String>

    /*
    * Prepare individual member vaccination record
    * */
    private fun getIndividualVaccinationData(data: ImmunizationRecord): MutableList<VaccineData> {
        val vaccineDataList: MutableList<VaccineData> = mutableListOf()

        data.immunizationEntries?.forEachIndexed { index, entry ->

            val vaccineInfo =
                vaccineInfo.filter { it.key == entry.resource.vaccineCode.toString() }

            val productInfo = if (vaccineInfo.isEmpty())
                "Not Available"
            else
                vaccineInfo.values.first()

            vaccineDataList.add(
                VaccineData(
                    data.name,
                    (index + 1).toString(),
                    entry.resource.occurrenceDateTime,
                    productInfo,
                    "Not Available",
                    entry.resource.performer?.last()?.actor?.display,
                    entry.resource.lotNumber
                )
            )
        }

        return vaccineDataList
    }

    /*
    * Fetch the covid test result
    * */
    suspend fun getCovidTestResult(phn: String, dob: String, dot: Any) {

        responseMutableSharedFlow.emit(Response.Loading())

        // TODO: 26/11/21 Network call to be implemented

        saveCovidTestResult(
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
    }

    /*
    * Save covid test results in DB
    * */
    private suspend fun saveCovidTestResult(covidTestResult: CovidTestResult) {

        dataSource.insertCovidTestResult(
            covidTestResult
        )

        responseMutableSharedFlow.emit(Response.Success(covidTestResult.patientDisplayName))
    }

    fun fetchHealthRecordFromHealthCard(healthCard: HealthCard): ImmunizationRecord? {

        try {
            return shcDecoder.getImmunizationStatus(healthCard.uri)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }
}

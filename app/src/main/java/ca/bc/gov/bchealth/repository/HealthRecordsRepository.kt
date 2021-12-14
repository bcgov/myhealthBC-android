package ca.bc.gov.bchealth.repository

import ca.bc.gov.bchealth.data.local.entity.CovidTestResult
import ca.bc.gov.bchealth.data.local.entity.HealthCard
import ca.bc.gov.bchealth.datasource.LocalDataSource
import ca.bc.gov.bchealth.model.ImmunizationRecord
import ca.bc.gov.bchealth.model.healthrecords.HealthRecord
import ca.bc.gov.bchealth.model.healthrecords.VaccineData
import ca.bc.gov.bchealth.model.network.responses.covidtests.Record
import ca.bc.gov.bchealth.model.network.responses.covidtests.ResourcePayload
import ca.bc.gov.bchealth.model.network.responses.covidtests.ResponseCovidTests
import ca.bc.gov.bchealth.services.LaboratoryServices
import ca.bc.gov.bchealth.utils.ErrorData
import ca.bc.gov.bchealth.utils.Response
import ca.bc.gov.bchealth.utils.SHCDecoder
import ca.bc.gov.bchealth.utils.getDateOfCollection
import ca.bc.gov.bchealth.utils.getIssueDate
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine

/*
* Created by amit_metri on 26,November,2021
*/
class HealthRecordsRepository @Inject constructor(
    private val dataSource: LocalDataSource,
    private val shcDecoder: SHCDecoder,
    private val laboratoryServices: LaboratoryServices
) {

    /*
    * Used to manage Success, Error and Loading status in the UI
    * */
    private val responseMutableSharedFlow = MutableSharedFlow<Response<String>>()
    val responseSharedFlow: SharedFlow<Response<String>>
        get() = responseMutableSharedFlow.asSharedFlow()

    /*
    * Health Records
    * */
    val healthRecords: Flow<List<HealthRecord>> =

        dataSource.getCards()
            .combine(dataSource.getCovidTestResults()) { healthPasses, covidTestResults ->

                val healthRecordList: MutableList<HealthRecord> = mutableListOf()

                healthPasses.forEach { healthPass ->

                    try {
                        val data = shcDecoder.getImmunizationStatus(healthPass.uri)

                        /*
                        * Add common data, vaccination data and covid test results data to health record of a member
                        * */
                        healthRecordList.add(
                            HealthRecord(
                                healthPass.id,
                                data.name,
                                data.status,
                                data.issueDate.getIssueDate(),
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
                                null,
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

                healthRecordList
            }

    private val vaccineInfo: HashMap<String, String> = mapOf(
        "28581000087106" to "PFIZER-BIONTECH COMIRNATY",
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

            var value = ""

            entry.resource.vaccineCode?.coding?.forEach { coding ->
                if (vaccineInfo.keys.contains(coding.code))
                    value = vaccineInfo.getValue(coding.code)
            }

            val productInfo = if (value.isEmpty())
                "Not Available"
            else
                value

            vaccineDataList.add(
                VaccineData(
                    null,
                    LocalDate.parse(
                        entry.resource.occurrenceDateTime,
                        DateTimeFormatter.ofPattern("yyyy-MM-dd")
                    ),
                    productInfo,
                    entry.resource.performer?.last()?.actor?.display,
                    entry.resource.lotNumber
                )
            )
        }

        vaccineDataList.sortBy { it.occurrenceDate }

        vaccineDataList.mapIndexed { idx, item ->
            item.doseNumber = "Dose".plus(" ").plus(idx + 1)
        }

        return vaccineDataList
    }

    /*
    * Fetch the covid test result
    * */
    suspend fun getCovidTestResult(phn: String, dob: String, collectionDate: String) {

        responseMutableSharedFlow.emit(Response.Loading())

        loop@ for (i in RETRY_COUNT downTo 1) {

            val result = laboratoryServices.getCovidTests(
                phn, dob, collectionDate
            )

            if (!validateResponseCovidTests(result)) {
                responseMutableSharedFlow.emit(Response.Error(ErrorData.GENERIC_ERROR))
                break@loop
            }

            val responseCovidTests = result.body()

            /*
             * Loaded field will return false when HGS will respond with cache data.
             * HGS response also provide the retry time after which updated data is available.
             * */
            if (responseCovidTests?.resourcePayload?.loaded == false) {

                responseCovidTests.resourcePayload.retryin.toLong().let {
                    delay(it)
                }

                if (i == 1) {
                    responseMutableSharedFlow.emit(Response.Error(ErrorData.GENERIC_ERROR))
                    break@loop
                } else {
                    continue@loop
                }
            }

            if (!validateResourcePayload(responseCovidTests?.resourcePayload)) {
                responseMutableSharedFlow.emit(Response.Error(ErrorData.GENERIC_ERROR))
                break@loop
            }

            val covidTestResults: MutableList<CovidTestResult> = mutableListOf()
            responseCovidTests?.resourcePayload?.records?.forEach { record ->
                covidTestResults.add(record.parseToCovidTestResult())
            }

            saveCovidTestResult(covidTestResults)
        }

        // TODO: 08/12/21 Remove dummy data once API starts working
        /*saveCovidTestResult(
            listOf(
                CovidTestResult(
                    toString(),
                    "USER NAME",
                    "Freshworks lab",
                    Date.valueOf("2021-10-10"),
                    Date.valueOf("2021-10-11"),
                    "Covid Test",
                    "Test Type",
                    "COMPLETED",
                    "POSITIVE",
                    "Tested Positive",
                    "Tested positive description",
                    "link"
                )
            )
        )*/
    }

    private fun validateRecords(records: List<Record>): Boolean {

        records.forEach { record ->

            record.apply {
                if (collectionDateTime.isNullOrEmpty() ||
                    lab.isNullOrEmpty() ||
                    patientDisplayName.isNullOrEmpty() ||
                    reportId.isNullOrEmpty() ||
                    resultDateTime.isNullOrEmpty() ||
                    resultDescription.isNullOrEmpty() ||
                    resultLink.isNullOrEmpty() ||
                    resultTitle.isNullOrEmpty() ||
                    testName.isNullOrEmpty() ||
                    testOutcome.isNullOrEmpty() ||
                    testStatus.isNullOrEmpty() ||
                    testType.isNullOrEmpty()
                ) {
                    return false
                }

                if (resultDateTime.getDateOfCollection() == null ||
                    testOutcome.getDateOfCollection() == null
                )
                    return false
            }
        }

        return true
    }

    private fun validateResourcePayload(resourcePayload: ResourcePayload?): Boolean {
        if (resourcePayload?.records.isNullOrEmpty()) {
            return false
        }

        resourcePayload?.records?.let {
            if (!validateRecords(it)) {
                return false
            }
        }

        return true
    }

    private fun validateResponseCovidTests(result: retrofit2.Response<ResponseCovidTests>):
        Boolean {

        if (!result.isSuccessful)
            return false

        if (result.body() == null)
            return false

        val responseCovidTests = result.body()
        if (responseCovidTests?.resourcePayload == null)
            return false

        return true
    }

    /*
    * Save covid test results in DB
    * */
    private suspend fun saveCovidTestResult(covidTestResults: List<CovidTestResult>) {

        dataSource.insertCovidTests(
            covidTestResults
        )

        responseMutableSharedFlow.emit(
            Response.Success(covidTestResults.first().patientDisplayName)
        )
    }

    fun fetchHealthRecordFromHealthCard(healthCard: HealthCard): ImmunizationRecord? {

        try {
            return shcDecoder.getImmunizationStatus(healthCard.uri)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    companion object {
        const val RETRY_COUNT = 3
    }
}

private fun Record.parseToCovidTestResult(): CovidTestResult {

    return CovidTestResult(
        this.reportId.toString(),
        this.patientDisplayName.toString(),
        this.lab.toString(),
        this.collectionDateTime?.getDateOfCollection()!!,
        this.resultDateTime?.getDateOfCollection()!!,
        this.testName.toString(),
        this.testType.toString(),
        this.testStatus.toString(),
        this.testOutcome.toString(),
        this.resultTitle.toString(),
        this.resultLink.toString()
    )
}

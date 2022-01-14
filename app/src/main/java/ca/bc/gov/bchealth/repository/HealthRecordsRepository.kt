package ca.bc.gov.bchealth.repository

import ca.bc.gov.bchealth.data.local.entity.CovidTestResult
import ca.bc.gov.bchealth.data.local.entity.HealthCard
import ca.bc.gov.bchealth.datasource.LocalDataSource
import ca.bc.gov.bchealth.model.ImmunizationRecord
import ca.bc.gov.bchealth.model.healthrecords.HealthRecord
import ca.bc.gov.bchealth.model.healthrecords.IndividualRecord
import ca.bc.gov.bchealth.model.healthrecords.VaccineData
import ca.bc.gov.bchealth.model.network.responses.covidtests.Record
import ca.bc.gov.bchealth.model.network.responses.covidtests.ResponseCovidTests
import ca.bc.gov.bchealth.services.LaboratoryServices
import ca.bc.gov.bchealth.ui.healthrecords.IndividualHealthRecordViewModel
import ca.bc.gov.bchealth.utils.SHCDecoder
import ca.bc.gov.bchealth.utils.getDateForIndividualCovidTestResult
import ca.bc.gov.bchealth.utils.getDateForIndividualVaccineRecord
import ca.bc.gov.bchealth.utils.getIssueDate
import ca.bc.gov.bchealth.utils.getLocalDateTimeFromAPIResponse
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject

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

    val individualHealthRecords: Flow<List<IndividualRecord>> =
        dataSource.getCards()
            .combine(dataSource.getCovidTestResults()) { healthPasses, covidTestResults ->

                val individualRecords: MutableList<IndividualRecord> = mutableListOf()

                healthPasses.forEach { healthPass ->

                    try {
                        val data = shcDecoder.getImmunizationStatus(healthPass.uri)
                        individualRecords.add(
                            IndividualRecord(
                                "Covid-19 vaccination",
                                getSubTitleForVaccinationRecord(data),
                                data.name,
                                data.status,
                                data.issueDate.getIssueDate(),
                                HealthRecordType.VACCINE_RECORD,
                                healthPass.id,
                                getIndividualVaccinationData(data),
                                mutableListOf()
                            )
                        )
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                val filteredResults = covidTestResults.groupBy { it.combinedReportId }

                filteredResults.forEach { result ->
                    individualRecords.add(
                        IndividualRecord(
                            "Covid-19 test result",
                            getSubTitleForCovidTestResult(result),
                            result.value.first().patientDisplayName,
                            null,
                            "",
                            HealthRecordType.COVID_TEST_RECORD,
                            vaccineDataList = mutableListOf(),
                            covidTestResultList = result.value
                        )
                    )
                }

                individualRecords
            }

    private fun getSubTitleForCovidTestResult(result: Map.Entry<String, List<CovidTestResult>>): String? {
        return if (result.value.first().testStatus == "Pending") {
            result.value.first().testStatus
                .plus(IndividualHealthRecordViewModel.bulletPoint)
                .plus(result.value.first().resultDateTime.getDateForIndividualCovidTestResult())
        } else {
            result.value.first().testOutcome
                .plus(IndividualHealthRecordViewModel.bulletPoint)
                .plus(result.value.first().resultDateTime.getDateForIndividualCovidTestResult())
        }
    }

    private fun getSubTitleForVaccinationRecord(data: ImmunizationRecord): String? {
        return data.status.value
            .plus(IndividualHealthRecordViewModel.bulletPoint)
            .plus(
                getIndividualVaccinationData(data).last().occurrenceDate
                    ?.getDateForIndividualVaccineRecord()
            )
    }

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

    private var retryCount = 0

    /*
    * Fetch the covid test result
    * */
    suspend fun getCovidTestResult(phn: String, dob: String, collectionDate: String) {

        retryCount = 0

        responseMutableSharedFlow.emit(Response.Loading())

        val map = mapOf(
            "phn" to phn,
            "dob" to dob,
            "collectionDate" to collectionDate
        )

        initiateNetWorkCall(map)
    }

    private suspend fun initiateNetWorkCall(map: Map<String, String>) {

        val result = laboratoryServices.getCovidTests(
            map.getValue("phn"),
            map.getValue("dob"),
            map.getValue("collectionDate")
        )

        if (!validateResponseCovidTests(result)) {
            responseMutableSharedFlow.emit(Response.Error(ErrorData.GENERIC_ERROR))
            return
        }

        checkWhetherRetryRequired(result.body(), map)
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

    private suspend fun checkWhetherRetryRequired(
        responseCovidTests: ResponseCovidTests?,
        map: Map<String, String>
    ) {

        /*
        * Loaded field will return false when HGS will respond with cache data.
        * HGS response also provide the retry time after which updated data is available.
        * */
        if (responseCovidTests?.resourcePayload?.loaded == false) {

            retryCount++
            if (retryCount == RETRY_COUNT) {
                responseMutableSharedFlow.emit(Response.Error(ErrorData.GENERIC_ERROR))
                return
            } else {
                responseCovidTests.resourcePayload.retryin.toLong().let {
                    delay(it)
                    initiateNetWorkCall(map)
                }
            }
        } else {
            checkForKnownErrors(responseCovidTests, map)
        }
    }

    private suspend fun checkForKnownErrors(
        responseCovidTests: ResponseCovidTests?,
        map: Map<String, String>
    ) {

        if (responseCovidTests?.resultError != null) {

            when (responseCovidTests.resultError.actionCode) {

                ErrorCodes.MISMATCH.name -> {
                    responseMutableSharedFlow.emit(Response.Error(ErrorData.MISMATCH_ERROR))
                }

                ErrorCodes.INVALID.name -> {
                    responseMutableSharedFlow.emit(Response.Error(ErrorData.INVALID_PHN))
                }

                else -> {
                    validateResourcePayload(responseCovidTests, map)
                }
            }
        } else {
            validateResourcePayload(responseCovidTests, map)
        }
    }

    private suspend fun validateResourcePayload(
        responseCovidTests: ResponseCovidTests?,
        map: Map<String, String>
    ) {

        if (responseCovidTests?.resourcePayload?.records.isNullOrEmpty()) {
            responseMutableSharedFlow.emit(Response.Error(ErrorData.GENERIC_ERROR))
            return
        }

        responseCovidTests?.resourcePayload?.records?.let {
            if (!validateRecords(it)) {
                responseMutableSharedFlow.emit(Response.Error(ErrorData.GENERIC_ERROR))
                return
            }
        }

        val covidTestResults: MutableList<CovidTestResult> = mutableListOf()
        var combinedReportId = ""

        responseCovidTests?.resourcePayload?.records?.forEach {
            combinedReportId = combinedReportId.plus(it.reportId)
        }
        responseCovidTests?.resourcePayload?.records?.forEach { record ->
            covidTestResults.add(record.parseToCovidTestResult(combinedReportId, map))
        }

        checkForDuplicateRecord(covidTestResults, combinedReportId)
    }

    private fun validateRecords(records: List<Record>): Boolean {

        records.forEach { record ->

            record.apply {
                if (collectionDateTime.isNullOrEmpty() ||
                    lab.isNullOrEmpty() ||
                    patientDisplayName.isNullOrEmpty() ||
                    reportId.isNullOrEmpty() ||
                    resultDateTime.isNullOrEmpty() ||
                    // resultLink.isNullOrEmpty() ||
                    // resultTitle.isNullOrEmpty() ||
                    testName.isNullOrEmpty() ||
                    testOutcome.isNullOrEmpty() ||
                    testStatus.isNullOrEmpty()
                ) {
                    return false
                }

                if (collectionDateTime.getLocalDateTimeFromAPIResponse() == null ||
                    resultDateTime.getLocalDateTimeFromAPIResponse() == null
                ) {
                    return false
                }
            }
        }

        return true
    }

    private suspend fun checkForDuplicateRecord(
        covidTestResults: MutableList<CovidTestResult>,
        combinedReportId: String
    ) {
        if (dataSource.getMatchingCovidTestResultsCount(combinedReportId) > 0) {
            responseMutableSharedFlow.emit(Response.Error(ErrorData.DUPLICATE_RECORD))
            return
        } else {
            saveCovidTestResult(covidTestResults)
        }
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

enum class HealthRecordType {
    VACCINE_RECORD,
    COVID_TEST_RECORD
}

private fun Record.parseToCovidTestResult(
    combinedReportId: String,
    map: Map<String, String>
): CovidTestResult {

    return CovidTestResult(
        this.reportId.toString(),
        this.patientDisplayName.toString(),
        this.lab.toString(),
        this.collectionDateTime?.getLocalDateTimeFromAPIResponse()!!,
        this.resultDateTime?.getLocalDateTimeFromAPIResponse()!!,
        this.testName.toString(),
        this.testType.toString(),
        this.testStatus.toString(),
        this.testOutcome.toString(),
        this.resultTitle.toString(),
        this.resultLink.toString(),
        combinedReportId,
        map.getValue("phn"),
        map.getValue("dob")
    )
}

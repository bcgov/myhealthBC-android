package ca.bc.gov.bchealth.workers

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import ca.bc.gov.bchealth.usecases.FetchCommentsUseCase
import ca.bc.gov.bchealth.usecases.records.FetchClinicalDocumentsUseCase
import ca.bc.gov.bchealth.usecases.records.FetchCovidOrdersUseCase
import ca.bc.gov.bchealth.usecases.records.FetchHealthVisitsUseCase
import ca.bc.gov.bchealth.usecases.records.FetchHospitalVisitsUseCase
import ca.bc.gov.bchealth.usecases.records.FetchImmunizationsUseCase
import ca.bc.gov.bchealth.usecases.records.FetchLabOrdersUseCase
import ca.bc.gov.bchealth.usecases.records.FetchMedicationsUseCase
import ca.bc.gov.bchealth.usecases.records.FetchPatientDataUseCase
import ca.bc.gov.bchealth.usecases.records.FetchSpecialAuthoritiesUseCase
import ca.bc.gov.bchealth.usecases.records.FetchVaccinesUseCase
import ca.bc.gov.common.BuildConfig.LOCAL_API_VERSION
import ca.bc.gov.common.model.AuthParametersDto
import ca.bc.gov.common.model.dependents.DependentDto
import ca.bc.gov.repository.DependentsRepository
import ca.bc.gov.repository.PatientWithBCSCLoginRepository
import ca.bc.gov.repository.UserProfileRepository
import ca.bc.gov.repository.bcsc.BcscAuthRepo
import ca.bc.gov.repository.bcsc.PostLoginCheck
import ca.bc.gov.repository.di.IoDispatcher
import ca.bc.gov.repository.patient.PatientRepository
import ca.bc.gov.repository.worker.MobileConfigRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.withContext

/*
* Created by amit_metri on 16,February,2022
*/
@HiltWorker
class FetchAuthenticatedHealthRecordsWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted workerParams: WorkerParameters,
    private val bcscAuthRepo: BcscAuthRepo,
    @IoDispatcher private val dispatcher: CoroutineDispatcher,
    private val patientRepository: PatientRepository,
    private val dependentsRepository: DependentsRepository,
    private val patientWithBCSCLoginRepository: PatientWithBCSCLoginRepository,
    private val userProfileRepository: UserProfileRepository,
    private val mobileConfigRepository: MobileConfigRepository,
    private val fetchMedicationsUseCase: FetchMedicationsUseCase,
    private val fetchLabOrdersUseCase: FetchLabOrdersUseCase,
    private val fetchCovidOrdersUseCase: FetchCovidOrdersUseCase,
    private val fetchImmunizationsUseCase: FetchImmunizationsUseCase,
    private val fetchCommentsUseCase: FetchCommentsUseCase,
    private val fetchHealthVisitsUseCase: FetchHealthVisitsUseCase,
    private val fetchHospitalVisitsUseCase: FetchHospitalVisitsUseCase,
    private val fetchSpecialAuthoritiesUseCase: FetchSpecialAuthoritiesUseCase,
    private val fetchClinicalDocumentsUseCase: FetchClinicalDocumentsUseCase,
    private val fetchVaccinesUseCase: FetchVaccinesUseCase,
    private val patientDataUseCase: FetchPatientDataUseCase
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        val remoteVersion: Int
        try {
            remoteVersion = mobileConfigRepository.getRemoteApiVersion()
        } catch (e: Exception) {
            return respondToFailure(FailureReason.IS_HG_SERVICES_UP, false)
        }

        if (LOCAL_API_VERSION < remoteVersion) {
            return respondToFailure(FailureReason.APP_UPDATE_REQUIRED, true)
        }

        if (bcscAuthRepo.getPostLoginCheck() == PostLoginCheck.IN_PROGRESS.name ||
            !bcscAuthRepo.checkSession()
        ) {
            return Result.failure()
        }
        return fetchAuthRecords()
    }

    private suspend fun fetchAuthRecords(): Result {
        val dependents: List<DependentDto>?
        val authParameters = bcscAuthRepo.getAuthParametersDto()
        val patientId: Long

        try {
            val patient = patientWithBCSCLoginRepository.getPatient(
                authParameters.token, authParameters.hdid
            )

            patientId = patientRepository.insertAuthenticatedPatient(patient)
        } catch (e: Exception) {
            e.printStackTrace()
            return Result.failure()
        }

        try {
            dependents = fetchRecord(authParameters, dependentsRepository::fetchAllDependents)
            dependents?.let { dependentsRepository.storeDependents(it, guardianId = patientId) }
        } catch (e: Exception) {
            e.printStackTrace()
            return Result.failure()
        }

        val isApiFailed = loadRecords(patientId, authParameters, dependents)

        return if (isApiFailed) {
            return respondToFailure(FailureReason.IS_RECORD_FETCH_FAILED, true)
        } else {
            Result.success()
        }
    }

    private suspend fun loadRecords(
        patientId: Long,
        authParameters: AuthParametersDto,
        dependents: List<DependentDto>?
    ): Boolean {
        val isApiFailed: Boolean
        setProgress(workDataOf(RECORD_FETCH_STARTED to true))
        withContext(dispatcher) {
            val taskResults = listOf(
                async { fetchMedicationsUseCase.execute(patientId, authParameters) },
                runTaskAsync {
                    fetchVaccinesUseCase.execute(patientId, authParameters, dependents)
                },
                runTaskAsync { fetchLabOrdersUseCase.execute(patientId, authParameters) },
                runTaskAsync { fetchCovidOrdersUseCase.execute(patientId, authParameters) },
                runTaskAsync { fetchImmunizationsUseCase.execute(patientId, authParameters) },
                runTaskAsync { fetchHealthVisitsUseCase.execute(patientId, authParameters) },
                runTaskAsync { fetchClinicalDocumentsUseCase.execute(patientId, authParameters) },
                runTaskAsync { fetchHospitalVisitsUseCase.execute(patientId, authParameters) },
                runTaskAsync { fetchCommentsUseCase.execute(authParameters) },
                runTaskAsync { fetchSpecialAuthoritiesUseCase.execute(patientId, authParameters) },
                runTaskAsync { userProfileRepository.deleteUserProfileCache(patientId) },
                runTaskAsync { patientDataUseCase.execute(patientId, authParameters) }
            ).awaitAll()

            isApiFailed = taskResults.contains(Result.failure())
        }
        return isApiFailed
    }

    private suspend fun <T> fetchRecord(
        authParameters: AuthParametersDto,
        action: suspend (String, String) -> T?
    ): T? {
        var response: T?
        withContext(dispatcher) {
            response = action.invoke(authParameters.token, authParameters.hdid)
        }
        return response
    }

    private fun respondToFailure(reason: FailureReason, result: Boolean): Result {
        return Result.failure(
            Data.Builder()
                .putBoolean(reason.value, result)
                .build()
        )
    }

    private fun CoroutineScope.runTaskAsync(task: suspend () -> Unit): Deferred<Result> =
        this.async {
            try {
                task.invoke()
                Result.success()
            } catch (e: Exception) {
                Log.e("RecordsWorker", "Handling Exception:")
                e.printStackTrace()
                Result.failure()
            }
        }

    companion object {
        const val RECORD_FETCH_STARTED = "started"
    }

    enum class FailureReason(val value: String) {
        APP_UPDATE_REQUIRED("appUpdateRequired"),
        IS_HG_SERVICES_UP("isHgServicesUp"),
        IS_RECORD_FETCH_FAILED("IS_RECORD_FETCH_FAILED")
    }
}

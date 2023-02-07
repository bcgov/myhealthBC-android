package ca.bc.gov.bchealth.workers

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.WorkerParameters
import ca.bc.gov.bchealth.usecases.FetchCommentsUseCase
import ca.bc.gov.bchealth.usecases.RefreshMobileConfigUseCase
import ca.bc.gov.bchealth.usecases.records.FetchClinicalDocumentsUseCase
import ca.bc.gov.bchealth.usecases.records.FetchCovidOrdersUseCase
import ca.bc.gov.bchealth.usecases.records.FetchHealthVisitsUseCase
import ca.bc.gov.bchealth.usecases.records.FetchHospitalVisitsUseCase
import ca.bc.gov.bchealth.usecases.records.FetchImmunizationsUseCase
import ca.bc.gov.bchealth.usecases.records.FetchLabOrdersUseCase
import ca.bc.gov.bchealth.usecases.records.FetchMedicationsUseCase
import ca.bc.gov.bchealth.usecases.records.FetchSpecialAuthoritiesUseCase
import ca.bc.gov.bchealth.usecases.records.FetchVaccinesUseCase
import ca.bc.gov.common.BuildConfig.LOCAL_API_VERSION
import ca.bc.gov.common.R
import ca.bc.gov.common.model.AuthParametersDto
import ca.bc.gov.common.model.dependents.DependentDto
import ca.bc.gov.repository.DependentsRepository
import ca.bc.gov.repository.PatientWithBCSCLoginRepository
import ca.bc.gov.repository.bcsc.BcscAuthRepo
import ca.bc.gov.repository.bcsc.PostLoginCheck
import ca.bc.gov.repository.di.IoDispatcher
import ca.bc.gov.repository.patient.PatientRepository
import ca.bc.gov.repository.utils.NotificationHelper
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
    private val notificationHelper: NotificationHelper,
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
    private val refreshMobileConfigUseCase: RefreshMobileConfigUseCase
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        val remoteVersion = mobileConfigRepository.getRemoteApiVersion()
        if (LOCAL_API_VERSION < remoteVersion) {
            return respondToAppUpdateRequired()
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
            refreshMobileConfigUseCase.execute()
        } catch (e: Exception) {
            return respondToHgServicesDown()
        }

        notificationHelper.showNotification(context.getString(R.string.notification_title_while_fetching_data))

        try {
            val patient = patientWithBCSCLoginRepository.getPatient(
                authParameters.token, authParameters.hdid
            )

            patientId = patientRepository.insertAuthenticatedPatient(patient)
        } catch (e: Exception) {
            e.printStackTrace()
            updateNotification(isApiFailed = true)
            return Result.failure()
        }

        try {
            dependents = fetchRecord(authParameters, dependentsRepository::fetchAllDependents)
            dependents?.let { dependentsRepository.storeDependents(it, guardianId = patientId) }
        } catch (e: Exception) {
            e.printStackTrace()
            updateNotification(isApiFailed = true)
            return Result.failure()
        }

        val isApiFailed = loadRecords(patientId, authParameters, dependents)

        updateNotification(isApiFailed)

        return if (isApiFailed) {
            Result.failure()
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
            ).awaitAll()

            isApiFailed = taskResults.contains(Result.failure())
        }
        return isApiFailed
    }

    private fun updateNotification(isApiFailed: Boolean) {
        val notificationText = if (isApiFailed) {
            R.string.notification_title_on_failed
        } else {
            R.string.notification_title_on_success
        }
        notificationHelper.updateNotification(context.getString(notificationText))
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

    private fun respondToHgServicesDown(): Result {
        return Result.failure(
            Data.Builder()
                .putBoolean(IS_HG_SERVICES_UP, false)
                .build()
        )
    }

    private fun respondToAppUpdateRequired(): Result {
        return Result.failure(
            Data.Builder()
                .putBoolean(APP_UPDATE_REQUIRED, true)
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
        const val APP_UPDATE_REQUIRED = "appUpdateRequired"
        const val IS_HG_SERVICES_UP = "isHgServicesUp"
    }
}

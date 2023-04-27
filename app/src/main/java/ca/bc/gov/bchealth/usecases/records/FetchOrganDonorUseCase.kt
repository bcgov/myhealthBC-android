package ca.bc.gov.bchealth.usecases.records

import ca.bc.gov.common.BuildConfig
import ca.bc.gov.common.model.AuthParametersDto
import ca.bc.gov.repository.di.IoDispatcher
import ca.bc.gov.repository.services.OrganDonorRepository
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

class FetchOrganDonorUseCase @Inject constructor(
    private val organDonorRepository: OrganDonorRepository,
    @IoDispatcher private val dispatcher: CoroutineDispatcher,
) : BaseRecordUseCase(dispatcher) {

    suspend fun execute(
        patientId: Long,
        authParameters: AuthParametersDto
    ) {

        if (BuildConfig.FLAG_SERVICE_TAB.not()) return

        val organDonor = organDonorRepository.fetchOrganDonationStatus(authParameters.hdid, authParameters.token)

        organDonor.patientId = patientId
        organDonorRepository.insert(organDonor)
    }
}

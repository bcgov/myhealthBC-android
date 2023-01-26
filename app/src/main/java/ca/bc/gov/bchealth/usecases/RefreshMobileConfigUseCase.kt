package ca.bc.gov.bchealth.usecases

import ca.bc.gov.common.exceptions.ServiceDownException
import ca.bc.gov.repository.worker.MobileConfigRepository
import javax.inject.Inject

class RefreshMobileConfigUseCase @Inject constructor(
    private val repository: MobileConfigRepository
) {
    suspend fun execute() {
        val isHgServicesUp = repository.refreshMobileConfiguration()

        if (isHgServicesUp.not()) {
            throw ServiceDownException()
        }
    }
}

package ca.bc.gov.bchealth.usecases.records

import ca.bc.gov.common.exceptions.PartialRecordsException
import ca.bc.gov.common.model.AuthParametersDto
import ca.bc.gov.common.model.specialauthority.SpecialAuthorityDto
import ca.bc.gov.repository.di.IoDispatcher
import ca.bc.gov.repository.specialauthority.SpecialAuthorityRepository
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

class FetchSpecialAuthoritiesUseCase @Inject constructor(
    private val specialAuthorityRepository: SpecialAuthorityRepository,
    @IoDispatcher private val dispatcher: CoroutineDispatcher,
) : BaseRecordUseCase(dispatcher) {

    suspend fun execute(
        patientId: Long,
        authParameters: AuthParametersDto
    ) {
        val specialAuthorities = fetchRecord(
            authParameters, specialAuthorityRepository::getSpecialAuthority
        )

        var filteredOut = false
        val filteredList = specialAuthorities?.filter {
            val hasTitle = it.drugName.isNullOrBlank().not()
            if (hasTitle.not()) {
                filteredOut = true
            }
            hasTitle
        }

        insertSpecialAuthority(patientId, filteredList)
        if (filteredOut) {
            throw PartialRecordsException()
        }
    }

    private suspend fun insertSpecialAuthority(
        patientId: Long,
        specialAuthorities: List<SpecialAuthorityDto>?
    ) {
        specialAuthorityRepository.deleteSpecialAuthorities(patientId)
        specialAuthorities?.let { list ->
            list.forEach {
                it.patientId = patientId
            }
            specialAuthorityRepository.insert(list)
        }
    }
}

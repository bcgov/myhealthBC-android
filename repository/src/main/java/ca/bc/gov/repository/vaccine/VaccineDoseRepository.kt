package ca.bc.gov.repository.vaccine

import ca.bc.gov.common.model.VaccineDoseDto
import ca.bc.gov.data.datasource.VaccineDoseLocalDataSource
import javax.inject.Inject

/**
 * @author Pinakin Kansara
 */
class VaccineDoseRepository @Inject constructor(
    private val localDataSource: VaccineDoseLocalDataSource
) {

    suspend fun insertAllVaccineDose(id: Long, doses: List<VaccineDoseDto>): List<Long> =
        localDataSource.insertAllVaccineDoses(id, doses)

    suspend fun insertVaccineDose(dose: VaccineDoseDto): Long =
        localDataSource.insertVaccineDose(dose)

    suspend fun insertAllAuthenticatedVaccineDose(doses: List<VaccineDoseDto>): List<Long> =
        localDataSource.insertAllAuthenticatedVaccineDose(doses)

    suspend fun deleteVaccineDose(vaccineRecordId: Long): Int =
        localDataSource.deleteVaccineDose(vaccineRecordId)
}

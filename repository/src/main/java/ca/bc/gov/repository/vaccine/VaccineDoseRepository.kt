package ca.bc.gov.repository.vaccine

import ca.bc.gov.common.model.CreateVaccineDoseDto
import ca.bc.gov.common.model.VaccineDose
import ca.bc.gov.data.datasource.VaccineDoseLocalDataSource
import javax.inject.Inject

/**
 * @author Pinakin Kansara
 */
class VaccineDoseRepository @Inject constructor(
    private val localDataSource: VaccineDoseLocalDataSource
) {

    suspend fun insertAllVaccineDose(doses: List<CreateVaccineDoseDto>): List<Long> =
        localDataSource.insertAllVaccineDoses(doses)

    suspend fun insertVaccineDose(dose: CreateVaccineDoseDto): Long =
        localDataSource.insertVaccineDose(dose)

    suspend fun deleteVaccineDose(vaccineRecordId: Long): Int =
        localDataSource.deleteVaccineDose(vaccineRecordId)

    suspend fun getVaccineDoses(vaccineRecordId: Long): List<VaccineDose> = localDataSource.getVaccineDoses(vaccineRecordId)
}
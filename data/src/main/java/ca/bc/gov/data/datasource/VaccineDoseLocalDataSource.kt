package ca.bc.gov.data.datasource

import ca.bc.gov.common.model.CreateVaccineDoseDto
import ca.bc.gov.common.model.VaccineDose
import ca.bc.gov.data.local.dao.VaccineDoseDao
import ca.bc.gov.data.local.entity.VaccineDoseEntity
import ca.bc.gov.data.model.mapper.toDto
import ca.bc.gov.data.model.mapper.toEntity
import javax.inject.Inject

/**
 * @author Pinakin Kansara
 */
class VaccineDoseLocalDataSource @Inject constructor(
    private val vaccineDoseDao: VaccineDoseDao
) {

    /**
     * Insert [VaccineDoseEntity] in to database
     * @param vaccineDoseDto
     * @return vaccineRecordId else return -1L
     */
    suspend fun insertVaccineDose(vaccineDoseDto: CreateVaccineDoseDto): Long {
        val vaccineDoseId = vaccineDoseDao.insertVaccineDose(vaccineDoseDto.toEntity())
        if (vaccineDoseId == -1L) {
            return vaccineDoseDao.getVaccineDoseId(vaccineDoseDto.vaccineRecordId) ?: -1L
        }
        return vaccineDoseId
    }

    suspend fun insertAllVaccineDoses(doses: List<CreateVaccineDoseDto>): List<Long> {
        return vaccineDoseDao.insertAllVaccineDose(dose = doses.map { it.toEntity() })
    }

    /**
     * Delete [VaccineDoseEntity] from database
     * @param vaccineRecordId
     * @return number of row deleted else 0
     */
    suspend fun deleteVaccineDose(vaccineRecordId: Long): Int {
        return vaccineDoseDao.deleteVaccineDosesByRecordId(vaccineRecordId)
    }

    suspend fun getVaccineDoses(vaccineRecordId: Long): List<VaccineDose> =
        vaccineDoseDao.getVaccineDoses(vaccineRecordId).map {
            it.toDto()
        }
}
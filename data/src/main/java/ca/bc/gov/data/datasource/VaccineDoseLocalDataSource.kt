package ca.bc.gov.data.datasource

import ca.bc.gov.common.model.VaccineDoseDto
import ca.bc.gov.data.local.dao.VaccineDoseDao
import ca.bc.gov.data.local.entity.VaccineDoseEntity
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
    suspend fun insertVaccineDose(vaccineDoseDto: VaccineDoseDto): Long {
        val vaccineDoseId = vaccineDoseDao.insertVaccineDose(vaccineDoseDto.toEntity())
        if (vaccineDoseId == -1L) {
            return vaccineDoseDao.getVaccineDoseId(vaccineDoseDto.vaccineRecordId) ?: -1L
        }
        return vaccineDoseId
    }

    suspend fun insertAllVaccineDoses(id: Long, doses: List<VaccineDoseDto>): List<Long> {
        val vaccineDoses = vaccineDoseDao.getVaccineDoses(id)
        if (vaccineDoses.isEmpty()) {
            return vaccineDoseDao.insertAllVaccineDose(dose = doses.map { it.toEntity() })
        }
        return vaccineDoses.map { id }
    }

    suspend fun insertAllAuthenticatedVaccineDose(doses: List<VaccineDoseDto>): List<Long> {
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
}

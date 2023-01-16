package ca.bc.gov.data.datasource.local

import ca.bc.gov.common.model.hospitalvisits.HospitalVisitDto
import ca.bc.gov.data.datasource.local.dao.HospitalVisitDao
import ca.bc.gov.data.model.mapper.toEntity
import javax.inject.Inject

class HospitalVisitLocalDataSource @Inject constructor(
    private val hospitalVisitDao: HospitalVisitDao
) {

    suspend fun getHospitalVisit(hospitalVisitId: Long) =
        hospitalVisitDao.getHospitalVisitDetails(hospitalVisitId)

    suspend fun deleteHospitalVisits(patientId: Long) =
        hospitalVisitDao.delete(patientId)

    suspend fun insertHospitalVisits(list: List<HospitalVisitDto>) =
        hospitalVisitDao.insert(list.map { it.toEntity() })
}

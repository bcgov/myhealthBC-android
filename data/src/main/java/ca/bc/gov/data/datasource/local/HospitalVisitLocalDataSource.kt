package ca.bc.gov.data.datasource.local

import ca.bc.gov.data.datasource.local.dao.HospitalVisitDao
import javax.inject.Inject

class HospitalVisitLocalDataSource @Inject constructor(
    private val hospitalVisitDao: HospitalVisitDao
) {

    suspend fun getHospitalVisit() {
    }
}

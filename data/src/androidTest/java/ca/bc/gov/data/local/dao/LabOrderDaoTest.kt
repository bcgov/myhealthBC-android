package ca.bc.gov.data.local.dao

import ca.bc.gov.data.datasource.local.dao.LabOrderDao
import ca.bc.gov.data.datasource.local.dao.PatientDao
import ca.bc.gov.data.local.BaseDataBaseTest
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * @author Pinakin Kansara
 */
@HiltAndroidTest
class LabOrderDaoTest : BaseDataBaseTest() {

    private lateinit var patientDao: PatientDao
    private lateinit var laborDao: LabOrderDao

    override fun onCreate() {
        patientDao = db.getPatientDao()
        laborDao = db.getLabOrderDao()
    }

    @Test
    fun insertAndGetLabOrder() = runBlocking {

        val id = patientDao.insert(getPatient())

        val labOrderEntity = getLabOrder(patientId = id)

        laborDao.insert(labOrderEntity)

        val labOrder = laborDao.findById(labOrderEntity.id)

        assertEquals(labOrder.id, labOrderEntity.id)
    }

    override fun tearDown() {
    }
}

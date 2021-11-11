package ca.bc.gov.bchealth.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import ca.bc.gov.bchealth.data.local.dao.HealthCardDao
import ca.bc.gov.bchealth.data.local.entity.HealthCard

/**
 * [BcVaccineCardDataBase]
 *
 * @author Pinakin Kansara
 */
@Database(entities = [HealthCard::class], version = 2, exportSchema = true)
abstract class BcVaccineCardDataBase : RoomDatabase() {

    abstract fun getHealthCardDao(): HealthCardDao
}

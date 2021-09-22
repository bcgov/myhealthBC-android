package ca.bc.gov.bchealth.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import ca.bc.gov.bchealth.data.local.converter.CardTypeConverter
import ca.bc.gov.bchealth.data.local.dao.HealthCardDao
import ca.bc.gov.bchealth.data.local.entity.HealthCard

/**
 * [BcVaccineCardDataBase]
 *
 * @author Pinakin Kansara
 */
@Database(entities = [HealthCard::class], version = 1, exportSchema = true)
@TypeConverters(CardTypeConverter::class)
abstract class BcVaccineCardDataBase : RoomDatabase() {

    abstract fun getHealthCardDao(): HealthCardDao
}

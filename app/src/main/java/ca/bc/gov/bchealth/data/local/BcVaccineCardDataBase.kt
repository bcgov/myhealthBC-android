package ca.bc.gov.bchealth.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import ca.bc.gov.bchealth.data.local.dao.CovidTestResultDao
import ca.bc.gov.bchealth.data.local.dao.HealthCardDao
import ca.bc.gov.bchealth.data.local.entity.CovidTestResult
import ca.bc.gov.bchealth.data.local.entity.HealthCard

/**
 * [BcVaccineCardDataBase]
 *
 * @author Pinakin Kansara
 */
@Database(entities = [HealthCard::class, CovidTestResult::class], version = 3, exportSchema = true)
@TypeConverters(Converters::class)
abstract class BcVaccineCardDataBase : RoomDatabase() {

    abstract fun getHealthCardDao(): HealthCardDao

    abstract fun getCovidTestResultDao(): CovidTestResultDao

}

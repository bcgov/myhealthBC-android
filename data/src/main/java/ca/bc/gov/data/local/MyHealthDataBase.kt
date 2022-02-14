package ca.bc.gov.data.local

import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import ca.bc.gov.data.local.converter.DateTimeConverter
import ca.bc.gov.data.local.dao.PatientDao
import ca.bc.gov.data.local.dao.TestResultDao
import ca.bc.gov.data.local.dao.VaccineRecordDao
import ca.bc.gov.data.local.entity.PatientEntity
import ca.bc.gov.data.local.entity.TestRecordEntity
import ca.bc.gov.data.local.entity.TestResultEntity
import ca.bc.gov.data.local.entity.VaccineDoseEntity
import ca.bc.gov.data.local.entity.VaccineRecordEntity

/**
 * @author Pinakin Kansara
 */
@Database(
    entities = [PatientEntity::class, VaccineRecordEntity::class, TestResultEntity::class, TestRecordEntity::class, VaccineDoseEntity::class],
    version = 2,
    exportSchema = true,
    autoMigrations = [
        AutoMigration(from = 1, to = 2)
    ]
)
@TypeConverters(DateTimeConverter::class)
abstract class MyHealthDataBase : RoomDatabase() {

    abstract fun getPatientDao(): PatientDao

    abstract fun getVaccineRecordDao(): VaccineRecordDao

    abstract fun getTestResultDao(): TestResultDao
}

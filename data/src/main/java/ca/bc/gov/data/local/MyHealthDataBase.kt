package ca.bc.gov.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import ca.bc.gov.data.local.converter.DateTimeConverter
import ca.bc.gov.data.local.dao.PatientDao
import ca.bc.gov.data.local.dao.PatientWithVaccineRecordDao
import ca.bc.gov.data.local.dao.TestRecordsDao
import ca.bc.gov.data.local.dao.TestResultDao
import ca.bc.gov.data.local.dao.VaccineDoseDao
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
    version = 1,
    exportSchema = true
)
@TypeConverters(DateTimeConverter::class)
abstract class MyHealthDataBase : RoomDatabase() {

    abstract fun getPatientDao(): PatientDao

    abstract fun getVaccineRecordDao(): VaccineRecordDao

    abstract fun getVaccineDoseDao(): VaccineDoseDao

    abstract fun getTestResultDao(): TestResultDao

    abstract fun getTestRecordDao(): TestRecordsDao

    abstract fun getPatientWithVaccineRecordDao(): PatientWithVaccineRecordDao
}
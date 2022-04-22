package ca.bc.gov.data.datasource.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import ca.bc.gov.data.datasource.local.converter.AuthenticationStatusTypeConverter
import ca.bc.gov.data.datasource.local.converter.DateTimeConverter
import ca.bc.gov.data.datasource.local.dao.CommentDao
import ca.bc.gov.data.datasource.local.dao.CovidOrderDao
import ca.bc.gov.data.datasource.local.dao.CovidTestDao
import ca.bc.gov.data.datasource.local.dao.DispensingPharmacyDao
import ca.bc.gov.data.datasource.local.dao.ImmunizationForecastDao
import ca.bc.gov.data.datasource.local.dao.ImmunizationRecordDao
import ca.bc.gov.data.datasource.local.dao.LabOrderDao
import ca.bc.gov.data.datasource.local.dao.LabTestDao
import ca.bc.gov.data.datasource.local.dao.MedicationRecordDao
import ca.bc.gov.data.datasource.local.dao.MedicationSummaryDao
import ca.bc.gov.data.datasource.local.dao.PatientDao
import ca.bc.gov.data.datasource.local.dao.TestResultDao
import ca.bc.gov.data.datasource.local.dao.VaccineRecordDao
import ca.bc.gov.data.datasource.local.entity.PatientEntity
import ca.bc.gov.data.datasource.local.entity.comment.CommentEntity
import ca.bc.gov.data.datasource.local.entity.covid.CovidOrderEntity
import ca.bc.gov.data.datasource.local.entity.covid.CovidTestEntity
import ca.bc.gov.data.datasource.local.entity.covid.test.TestRecordEntity
import ca.bc.gov.data.datasource.local.entity.covid.test.TestResultEntity
import ca.bc.gov.data.datasource.local.entity.covid.vaccine.VaccineDoseEntity
import ca.bc.gov.data.datasource.local.entity.covid.vaccine.VaccineRecordEntity
import ca.bc.gov.data.datasource.local.entity.immunization.ImmunizationForecastEntity
import ca.bc.gov.data.datasource.local.entity.immunization.ImmunizationRecordEntity
import ca.bc.gov.data.datasource.local.entity.labtest.LabOrderEntity
import ca.bc.gov.data.datasource.local.entity.labtest.LabTestEntity
import ca.bc.gov.data.datasource.local.entity.medication.DispensingPharmacyEntity
import ca.bc.gov.data.datasource.local.entity.medication.MedicationRecordEntity
import ca.bc.gov.data.datasource.local.entity.medication.MedicationSummaryEntity

/**
 * @author Pinakin Kansara
 */
@Database(
    entities = [
        PatientEntity::class,
        VaccineRecordEntity::class,
        TestResultEntity::class,
        TestRecordEntity::class,
        VaccineDoseEntity::class,
        MedicationRecordEntity::class,
        MedicationSummaryEntity::class,
        DispensingPharmacyEntity::class,
        LabOrderEntity::class,
        LabTestEntity::class,
        CommentEntity::class,
        CovidOrderEntity::class,
        CovidTestEntity::class,
        ImmunizationRecordEntity::class,
        ImmunizationForecastEntity::class
    ],
    version = 1,
    exportSchema = true
)
@TypeConverters(DateTimeConverter::class, AuthenticationStatusTypeConverter::class)
abstract class MyHealthDataBase : RoomDatabase() {

    abstract fun getPatientDao(): PatientDao

    abstract fun getVaccineRecordDao(): VaccineRecordDao

    abstract fun getTestResultDao(): TestResultDao

    abstract fun getMedicationRecordDao(): MedicationRecordDao

    abstract fun getMedicationSummaryDao(): MedicationSummaryDao

    abstract fun getDispensingPharmacyDao(): DispensingPharmacyDao

    abstract fun getLabOrderDao(): LabOrderDao

    abstract fun getLabTestDao(): LabTestDao

    abstract fun getCommentDao(): CommentDao

    abstract fun getCovidOrderDao(): CovidOrderDao

    abstract fun getCovidTestDao(): CovidTestDao

    abstract fun getImmunizationRecordDao(): ImmunizationRecordDao

    abstract fun getImmunizationForecastDao(): ImmunizationForecastDao
}

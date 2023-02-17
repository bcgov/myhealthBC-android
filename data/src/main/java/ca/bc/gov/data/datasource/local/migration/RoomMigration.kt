package ca.bc.gov.data.datasource.local.migration

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

// Important: New migrations should be added to MigrationTest.kt
val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("ALTER TABLE comments ADD COLUMN is_uploaded INTEGER NOT NULL")
        database.execSQL("CREATE TABLE IF NOT EXISTS `immunization_record` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `patient_id` INTEGER NOT NULL, `immunization_id` TEXT, `date_of_immunization` INTEGER NOT NULL, `status` TEXT, `valid` INTEGER NOT NULL, `provider_clinic` TEXT, `targeted_disease` TEXT, `immunization_name` TEXT, `agent_code` TEXT, `agent_name` TEXT, `lotNumber` TEXT, `productName` TEXT, `data_source` TEXT NOT NULL, FOREIGN KEY(`patient_id`) REFERENCES `patient`(`id`) ON UPDATE CASCADE ON DELETE CASCADE )")
        database.execSQL("CREATE TABLE IF NOT EXISTS `immunization_forecast` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `immunization_record_id` INTEGER NOT NULL, `recommendation_id` TEXT, `create_date` INTEGER NOT NULL, `status` TEXT, `display_name` TEXT, `eligible_date` INTEGER NOT NULL, `due_date` INTEGER NOT NULL, FOREIGN KEY(`immunization_record_id`) REFERENCES `immunization_record`(`id`) ON UPDATE CASCADE ON DELETE CASCADE )")
        database.execSQL("CREATE TABLE IF NOT EXISTS `health_visits` (`health_visit_id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `patient_id` INTEGER NOT NULL, `id` TEXT, `encounter_date` INTEGER NOT NULL, `specialty_description` TEXT, `practitioner_name` TEXT, `data_source` TEXT NOT NULL, `name` TEXT, FOREIGN KEY(`patient_id`) REFERENCES `patient`(`id`) ON UPDATE CASCADE ON DELETE CASCADE )")
        database.execSQL("CREATE TABLE IF NOT EXISTS `special_authority` (`special_authority_id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `patient_id` INTEGER NOT NULL, `reference_number` TEXT, `drug_name` TEXT, `request_status` TEXT, `prescriber_first_name` TEXT, `prescriber_last_name` TEXT, `requested_date` INTEGER, `effective_date` INTEGER, `expiry_date` INTEGER, `data_source` TEXT NOT NULL, FOREIGN KEY(`patient_id`) REFERENCES `patient`(`id`) ON UPDATE CASCADE ON DELETE CASCADE )")
    }
}

val MIGRATION_2_3 = object : Migration(2, 3) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("CREATE TABLE IF NOT EXISTS `immunization_recommendation` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `patient_id` INTEGER NOT NULL, `recommendation_set_id` TEXT, `immunization_name` TEXT, `status` TEXT, `agentDueDate` INTEGER, `recommendedVaccinations` TEXT, FOREIGN KEY(`patient_id`) REFERENCES `patient`(`id`) ON UPDATE CASCADE ON DELETE CASCADE )")
    }
}

val MIGRATION_3_4 = object : Migration(3, 4) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("CREATE TABLE IF NOT EXISTS `dependent` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `patient_id` INTEGER NOT NULL, `guardian_id` INTEGER NOT NULL, `hdid` TEXT NOT NULL, `firstname` TEXT NOT NULL, `lastname` TEXT NOT NULL, `PHN` TEXT NOT NULL, `gender` TEXT NOT NULL, `dateOfBirth` INTEGER NOT NULL, `ownerId` TEXT NOT NULL, `delegateId` TEXT NOT NULL, `reasonCode` INTEGER NOT NULL, `version` INTEGER NOT NULL, `is_cache_valid` INTEGER NOT NULL, FOREIGN KEY(`patient_id`) REFERENCES `patient`(`id`) ON UPDATE CASCADE ON DELETE CASCADE , FOREIGN KEY(`guardian_id`) REFERENCES `patient`(`id`) ON UPDATE CASCADE ON DELETE CASCADE )")
        database.execSQL("CREATE TABLE IF NOT EXISTS `dependent_list_order` (`hdid` TEXT NOT NULL, `list_order` INTEGER NOT NULL, PRIMARY KEY(`hdid`))")
    }
}

val MIGRATION_4_5 = object : Migration(4, 5) {
    override fun migrate(database: SupportSQLiteDatabase) {
        val tableName = "hospital_visits"
        database.execSQL("CREATE TABLE IF NOT EXISTS `$tableName` (`hospital_visit_id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `patient_id` INTEGER NOT NULL, `health_service` TEXT NOT NULL, `location` TEXT NOT NULL, `provider` TEXT NOT NULL, `visit_type` TEXT NOT NULL, `visit_date` INTEGER NOT NULL, `discharge_date` INTEGER, FOREIGN KEY(`patient_id`) REFERENCES `patient`(`id`) ON UPDATE CASCADE ON DELETE CASCADE )")
    }
}

val MIGRATION_5_6 = object : Migration(5, 6) {
    override fun migrate(database: SupportSQLiteDatabase) {
        val tableName = "clinical_documents"
        database.execSQL("CREATE TABLE IF NOT EXISTS `$tableName` (`clinical_document_id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `patient_id` INTEGER NOT NULL, `fileId` TEXT NOT NULL, `name` TEXT NOT NULL, `type` TEXT NOT NULL, `facilityName` TEXT NOT NULL, `discipline` TEXT NOT NULL, `serviceDate` INTEGER NOT NULL, FOREIGN KEY(`patient_id`) REFERENCES `patient`(`id`) ON UPDATE CASCADE ON DELETE CASCADE )")
    }
}

val MIGRATION_6_7 = object : Migration(6, 7) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("DROP TABLE covid_order")
        database.execSQL("DROP TABLE covid_test")

        database.execSQL("CREATE TABLE `covid_order` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `covid_order_id` TEXT NOT NULL, `patient_id` INTEGER NOT NULL, `phn` TEXT, `ordering_provider_id` TEXT, `ordering_providers` TEXT, `reporting_lab` TEXT, `location` TEXT, `orm_or_oru` TEXT, `message_date_time` INTEGER NOT NULL, `message_id` TEXT, `additional_data` TEXT, `report_available` INTEGER NOT NULL, `data_source` TEXT NOT NULL, FOREIGN KEY(`patient_id`) REFERENCES `patient`(`id`) ON UPDATE CASCADE ON DELETE CASCADE )")
        database.execSQL("CREATE TABLE `covid_test` (`id` TEXT NOT NULL, `order_id` INTEGER NOT NULL, `test_type` TEXT, `out_of_range` INTEGER NOT NULL, `collected_date_time` INTEGER NOT NULL, `test_status` TEXT, `lab_result_outcome` TEXT, `result_description` TEXT, `result_link` TEXT, `received_date_time` INTEGER NOT NULL, `result_date_time` INTEGER NOT NULL, `loinc` TEXT, `loinc_name` TEXT, PRIMARY KEY(`id`), FOREIGN KEY(`order_id`) REFERENCES `covid_order`(`id`) ON UPDATE CASCADE ON DELETE CASCADE )")
    }
}

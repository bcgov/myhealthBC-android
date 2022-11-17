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
        database.execSQL("CREATE TABLE IF NOT EXISTS `dependent` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `patient_id` INTEGER NOT NULL, `guardian_id` INTEGER NOT NULL, `hdid` TEXT NOT NULL, `firstname` TEXT NOT NULL, `lastname` TEXT NOT NULL, `PHN` TEXT NOT NULL, `gender` TEXT NOT NULL, `dateOfBirth` INTEGER NOT NULL, `is_cache_valid` INTEGER NOT NULL, FOREIGN KEY(`patient_id`) REFERENCES `patient`(`id`) ON UPDATE CASCADE ON DELETE CASCADE , FOREIGN KEY(`guardian_id`) REFERENCES `patient`(`id`) ON UPDATE CASCADE ON DELETE CASCADE )")
    }
}

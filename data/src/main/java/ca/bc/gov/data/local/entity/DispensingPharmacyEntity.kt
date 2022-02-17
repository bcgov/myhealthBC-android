package ca.bc.gov.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

/**
 * @author Pinakin Kansara
 */
@Entity(
    tableName = "dispensing_pharmacy",
    foreignKeys = [
        ForeignKey(
            entity = MedicationRecordEntity::class,
            parentColumns = ["id"],
            childColumns = ["medication_record_id"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        )
    ]
)
data class DispensingPharmacyEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    @ColumnInfo(name = "medication_record_id")
    val medicationRecordId: Long,
    @ColumnInfo(name = "pharmacy_id")
    val pharmacyId: String?,
    val name: String?,
    @ColumnInfo(name = "address_line_1")
    val addressLine1: String?,
    @ColumnInfo(name = "address_line_2")
    val addressLine2: String?,
    val city: String?,
    val province: String?,
    @ColumnInfo(name = "postal_code")
    val postalCode: String?,
    @ColumnInfo(name = "country_code")
    val countryCode: String?,
    @ColumnInfo(name = "phone_number")
    val phoneNumber: String?,
    @ColumnInfo(name = "fax_number")
    val faxNumber: String?
)

package ca.bc.gov.data.datasource.local.entity.userprofile

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import ca.bc.gov.data.datasource.local.entity.PatientEntity

@Entity(
    tableName = "user_profile",
    foreignKeys = [
        ForeignKey(
            entity = PatientEntity::class,
            parentColumns = ["id"],
            childColumns = ["patient_id"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        )
    ]
)
data class UserProfileEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "user_profile_id")
    val userProfileId: Long = 0,

    @ColumnInfo(name = "patient_id")
    val patientId: Long,

    @ColumnInfo(name = "accepted_terms_of_service")
    val acceptedTermsOfService: Boolean,

    @ColumnInfo(name = "email")
    val email: String?,

    @ColumnInfo(name = "is_email_verified")
    val isEmailVerified: Boolean,

    @ColumnInfo(name = "sms_number")
    val smsNumber: String?,
)

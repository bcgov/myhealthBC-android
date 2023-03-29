package ca.bc.gov.data.datasource.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import ca.bc.gov.common.model.AuthenticationStatus
import java.time.Instant

/**
 * @author Pinakin Kansara
 */
@Entity(
    tableName = "patient",
    indices = [Index(value = ["full_name", "dob"], unique = true)]
)
data class PatientEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    @ColumnInfo(name = "full_name")
    val fullName: String,

    @ColumnInfo(name = "first_name")
    val firstName: String,

    @ColumnInfo(name = "last_name")
    val lastName: String,

    @ColumnInfo(name = "dob")
    val dateOfBirth: Instant,

    @ColumnInfo(name = "physical_address")
    val physicalAddress: PatientAddressEntity?,

    @ColumnInfo(name = "mailing_address")
    val mailingAddress: PatientAddressEntity?,

    @ColumnInfo(name = "phn")
    val phn: String? = null,

    @ColumnInfo(name = "time_stamp")
    val timeStamp: Instant = Instant.now(),

    @ColumnInfo(name = "patient_order", defaultValue = Long.MAX_VALUE.toString())
    val patientOrder: Long,

    @ColumnInfo(name = "authentication_status", defaultValue = "NON_AUTHENTICATED")
    val authenticationStatus: AuthenticationStatus = AuthenticationStatus.NON_AUTHENTICATED
)

data class PatientAddressEntity(
    @ColumnInfo(name = "street_lines")
    val streetLines: List<String>,

    @ColumnInfo(name = "city")
    val city: String,

    @ColumnInfo(name = "province")
    val province: String,

    @ColumnInfo(name = "postal_code")
    val postalCode: String,
)

data class PatientOrderUpdate(
    @ColumnInfo(name = "id")
    val id: Long,

    @ColumnInfo(name = "patient_order")
    val patientOrder: Long
)

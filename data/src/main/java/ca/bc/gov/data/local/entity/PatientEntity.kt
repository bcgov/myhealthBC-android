package ca.bc.gov.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.Instant

/**
 * @author Pinakin Kansara
 */
@Entity(
    tableName = "patient",
    indices = [Index(value = ["first_name", "last_name", "dob"], unique = true)]
)
data class PatientEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    @ColumnInfo(name = "first_name")
    val firstName: String,
    @ColumnInfo(name = "last_name")
    val lastName: String,
    @ColumnInfo(name = "dob")
    val dateOfBirth: Instant,
    val phn: String? = null,
    @ColumnInfo(name = "time_stamp")
    val timeStamp: Instant = Instant.now(),
    @ColumnInfo(name = "patient_order", defaultValue = Long.MAX_VALUE.toString())
    val patientOrder: Long
)

data class PatientOrderUpdate(
    val id: Long,
    @ColumnInfo(name = "patient_order")
    val patientOrder: Long
)

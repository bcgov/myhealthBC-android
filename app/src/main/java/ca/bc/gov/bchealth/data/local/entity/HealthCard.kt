package ca.bc.gov.bchealth.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * [HealthCard]
 *
 * @author Pinakin Kansara
 */
@Entity(
    tableName = "health_card",
    indices = [Index(value = ["uri"], unique = true)]
)
data class HealthCard(
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,
    var uri: String,
    var federalPass: String? = ""
)

package ca.bc.gov.bchealth.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * [HealthCard]
 *
 * @author Pinakin Kansara
 */
@Entity(tableName = "health_card")
data class HealthCard(
    @PrimaryKey
    val uri: String,
    val type: CardType
)

package ca.bc.gov.bchealth.data.local.converter

import androidx.room.TypeConverter
import ca.bc.gov.bchealth.data.local.entity.CardType

/**
 * [CardTypeConverter]
 *
 * @author Pinakin Kansara
 */
class CardTypeConverter {

    @TypeConverter
    fun toCardType(value: String) = enumValueOf<CardType>(value)

    @TypeConverter
    fun fromCardType(value: CardType) = value.name
}

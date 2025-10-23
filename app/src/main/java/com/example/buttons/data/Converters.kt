package com.example.buttons.data

import androidx.room.TypeConverter

class Converters {
    @TypeConverter
    fun fromPaymentType(value: PaymentType): String = value.name

    @TypeConverter
    fun toPaymentType(value: String): PaymentType = PaymentType.valueOf(value)

    @TypeConverter
    fun fromButtonSize(value: ButtonSize): String = value.name

    @TypeConverter
    fun toButtonSize(value: String): ButtonSize = ButtonSize.valueOf(value)
}

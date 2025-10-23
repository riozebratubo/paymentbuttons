package com.example.buttons.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "buttons")
data class ButtonEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val subtitle: String,
    val parcels: Int,
    val paymentType: PaymentType,
    val color: String,
    val size: ButtonSize,
    val position: Int
)

enum class PaymentType {
    CREDIT, DEBIT, USER_CHOICE
}

enum class ButtonSize {
    SMALL, NORMAL, BIG
}

package com.example.buttons.data

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.content.ContextCompat.startActivity
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
    val position: Int,
    val amount: String? = null,
    val pageId: Long = 1
) {
    /*
      Documentation on the Uri building: https://sdkandroid.stone.com.br/reference/explicacao-deeplink
    */
    fun getDeeplinkUri(userSelectedType: PaymentType? = null): Uri {
        val uriBuilder = Uri.Builder()
        uriBuilder.authority("pay")
        uriBuilder.scheme("payment-app")
        uriBuilder.appendQueryParameter("return_scheme", "return_scheme")
        amount?.let {
            uriBuilder.appendQueryParameter("amount", it.replace(".", "").replace(",", ""))
            uriBuilder.appendQueryParameter("editable_amount", "0")
        }
        userSelectedType?.let {
            uriBuilder.appendQueryParameter("transaction_type", paymentTypeToString(it))
        } ?: run {
            if (paymentType != PaymentType.USER_CHOICE) {
                uriBuilder.appendQueryParameter("transaction_type", paymentTypeToString(paymentType))
            }
        }
        if (parcels in 2..12) {
            uriBuilder.appendQueryParameter("installment_type", "MERCHANT")
            uriBuilder.appendQueryParameter("installment_count", parcels.toString())
        }
        // uriBuilder.appendQueryParameter("order_id", "123")
        return uriBuilder.build()
    }
}

enum class PaymentType {
    CREDIT, DEBIT, USER_CHOICE
}

fun paymentTypeToString(paymentType: PaymentType): String {
    return when (paymentType) {
        PaymentType.CREDIT -> "Credit"
        PaymentType.DEBIT -> "Debit"
        else -> ""
    }
}

enum class ButtonSize {
    SMALL, NORMAL, BIG
}

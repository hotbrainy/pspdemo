package ua.demianvolodymyr.pspdemo.model

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table

@Table
data class PaymentRequest(
    @Id var id: Long? = null, 
    val cardNumber: String,
    val expiryDate: String,
    val cvv: String,
    val amount: Double,
    val currency: String,
    var merchantId: String
)

fun PaymentRequest.toDto(): PaymentRequestDto = PaymentRequestDto(
    cardNumber = cardNumber,
    expiryDate = expiryDate,
    cvv = cvv,
    amount = amount,
    currency = currency
)

package ua.demianvolodymyr.pspdemo.model

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table

@Table
data class Transaction(
    @Id var id: Long? = null,
    val cardNumber: String,
    val status: String,
    val merchantId: String
)

fun Transaction.toDto(): TransactionDto = TransactionDto(
    cardNumber = cardNumber,
    status = status,
    merchantId = merchantId
)

enum class TransactionStatus {
    PENDING, APPROVED, DENIED
}
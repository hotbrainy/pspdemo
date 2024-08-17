package ua.demianvolodymyr.pspdemo.model

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table

@Table
data class PaymentResponse(
    @Id var id: Long? = null,
    val transactionId: String,
    val status: TransactionStatus,
    val message: String
)
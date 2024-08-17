package ua.demianvolodymyr.pspdemo.services

import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Component
import org.springframework.stereotype.Service
import ua.demianvolodymyr.pspdemo.model.*
import ua.demianvolodymyr.pspdemo.repository.TransactionRepository
import ua.demianvolodymyr.pspdemo.util.PaymentValidator
import java.util.*

@Service
class PaymentService(
    private val paymentValidator: PaymentValidator,
    private val transactionRepository: TransactionRepository
    ) {
    suspend fun processPayment(request: PaymentRequest): String {
        paymentValidator.validatePaymentRequest(request)
        val acquirer = selectAcquirer(request.cardNumber.take(6))
        val finalStatus = acquirer.processTransaction(request.cardNumber)

        val status = if (finalStatus == TransactionStatus.APPROVED) "Approved" else "Denied"
        val transactionId = UUID.randomUUID().toString()
        transactionRepository.save(Transaction(null, request.cardNumber, status, transactionId))
        return transactionId
    }


    private fun selectAcquirer(bin: String): MockAcquirer {
        val sumOfDigits = bin.sumOf { it.digitToInt() }
        return if (sumOfDigits % 2 == 0) {
            MockAcquirer("Acquirer A")
        } else {
            MockAcquirer("Acquirer B")
        }
    }
}
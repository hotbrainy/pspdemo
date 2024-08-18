package ua.demianvolodymyr.pspdemo.services

import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Component
import org.springframework.stereotype.Service
import ua.demianvolodymyr.pspdemo.model.*
import ua.demianvolodymyr.pspdemo.repository.TransactionRepository
import ua.demianvolodymyr.pspdemo.util.PaymentValidator
import java.util.*


/**
 * Service for processing payments.
 *
 * The `PaymentService` class handles the processing of payment requests. It validates the payment request,
 * selects an appropriate acquirer based on the card number, processes the transaction with the selected
 * acquirer, and saves the transaction details to the repository.
 *
 * @property paymentValidator The utility used for validating payment requests.
 * @property transactionRepository The repository for storing transaction details.
 */
@Service
class PaymentService(
    private val paymentValidator: PaymentValidator,
    private val transactionRepository: TransactionRepository
) {

    /**
     * Processes a payment request and returns a transaction ID.
     *
     * This function validates the payment request, selects an acquirer based on the card number,
     * processes the transaction, and saves the transaction details to the repository.
     *
     * @param request The payment request containing the card number and other payment details.
     *
     * @return A unique transaction ID for the processed payment.
     */
    suspend fun processPayment(request: PaymentRequest): String {
        try {
            paymentValidator.validatePaymentRequest(request)
            val acquirer = selectAcquirer(request.cardNumber.take(6))
            val finalStatus = acquirer.processTransaction(request.cardNumber)

            val status = if (finalStatus == TransactionStatus.APPROVED) "Approved" else "Denied"
            val transactionId = UUID.randomUUID().toString()
            transactionRepository.save(Transaction(null, request.cardNumber, status, transactionId))
            return transactionId
        } catch (e: Exception) {
            throw e
        }
    }


    /**
     * Selects an acquirer based on the BIN (Bank Identification Number).
     *
     * This function selects either "Acquirer A" or "Acquirer B" based on the sum of the digits
     * in the BIN. If the sum is even, "Acquirer A" is selected; otherwise, "Acquirer B" is selected.
     *
     * @param bin The first 6 digits of the card number (BIN) used to select the acquirer.
     *
     * @return The selected `MockAcquirer`.
     */
    private fun selectAcquirer(bin: String): MockAcquirer {
        val sumOfDigits = bin.sumOf { it.digitToInt() }
        return if (sumOfDigits % 2 == 0) {
            MockAcquirer("Acquirer A")
        } else {
            MockAcquirer("Acquirer B")
        }
    }
}
package ua.demianvolodymyr.pspdemo.util

import org.springframework.stereotype.Component
import ua.demianvolodymyr.pspdemo.model.PaymentRequest
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

@Component
class PaymentValidator {

    // Function to validate a given number using Luhn's algorithm
    private fun isValidCard(number: String): Boolean {
        val nDigits: Int = number.length
        var nSum: Int = 0
        var isSecond: Boolean = false
        for (i in nDigits - 1 downTo 0) {
            var d = number[i] - '0'
            if (isSecond) {
                d *= 2
            }

            // Add two digits to handle cases that result in two digits after doubling
            nSum += d / 10
            nSum += d % 10

            isSecond = !isSecond
        }

        return (nSum % 10 == 0)
    }


    private fun isValidExpiryDate(expiryDate: String): Boolean {
        val formatter = DateTimeFormatter.ofPattern("MM/yy")
        return try {
            val expiry = YearMonth.parse(expiryDate, formatter)
            expiry.isAfter(YearMonth.now())
        } catch (e: DateTimeParseException) {
            false
        }
    }

    private fun isValidCVV(cvv: String, cardNumber: String): Boolean {
        val expectedLength = if (cardNumber.startsWith("34") || cardNumber.startsWith("37")) 4 else 3
        return cvv.length == expectedLength && cvv.all { it.isDigit() }
    }


    private fun isValidCurrency(currency: String): Boolean {
        val supportedCurrencies = setOf("USD", "EUR", "GBP", "JPY", "CAD")
        return currency in supportedCurrencies
    }

    private fun isValidAmount(amount: Double): Boolean {
        return amount > 0 && String.format("%.2f", amount).toDouble() == amount
    }


    fun validatePaymentRequest(request: PaymentRequest) {
        require(isValidCard(request.cardNumber)) { "Invalid card number" }
        require(isValidExpiryDate(request.expiryDate)) { "Invalid expiry date" }
        require(isValidCVV(request.cvv, request.cardNumber)) { "Invalid CVV" }
        require(isValidCurrency(request.currency)) { "Invalid currency code" }
        require(isValidAmount(request.amount)) { "Invalid amount" }
    }
}
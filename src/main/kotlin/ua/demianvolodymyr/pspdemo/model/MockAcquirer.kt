package ua.demianvolodymyr.pspdemo.model

/**
 * Represents a mock acquirer that simulates transaction processing.
 *
 * The `MockAcquirer` class is used to simulate a payment acquirer that processes transactions.
 * It determines the transaction status based on the acquirer's name and the last digit of the card number.
 *
 * @property name The name of the mock acquirer (e.g., "Acquirer A").
 */

data class MockAcquirer(val name: String) {

    /**
     * Simulates the processing of a transaction based on the card number.
     *
     * This function determines the transaction status (`APPROVED` or `DENIED`) by examining the last digit
     * of the card number. If the acquirer's name is "Acquirer A," the transaction is approved if the last
     * digit is even; otherwise, it's denied. For other acquirers, the transaction is approved if the last
     * digit is odd.
     *
     * @param cardNumber The card number used for the transaction. The last digit is used to determine the
     *                   transaction status.
     *
     * @return `TransactionStatus.APPROVED` if the transaction is approved, otherwise `TransactionStatus.DENIED`.
     */
    fun processTransaction(cardNumber: String): TransactionStatus {
        var flg: Int = 1

        // If the acquirer is "Acquirer A," set the flag to 0.
        if (this.name == "Acquirer A") flg = 0

        // Determine the transaction status based on the last digit of the card number.
        return if (cardNumber.last().digitToInt() % 2 == flg) {
            TransactionStatus.APPROVED
        } else {
            TransactionStatus.DENIED
        }
    }
}
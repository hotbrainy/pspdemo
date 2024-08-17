package ua.demianvolodymyr.pspdemo.model

data class MockAcquirer(val name: String) {
    fun processTransaction(cardNumber: String): TransactionStatus {
        var flg : Int = 1
        if (this.name == "Acquirer A") flg = 0
        return if (cardNumber.last().digitToInt() % 2 == flg) {
            TransactionStatus.APPROVED
        } else {
            TransactionStatus.DENIED
        }
    }
}
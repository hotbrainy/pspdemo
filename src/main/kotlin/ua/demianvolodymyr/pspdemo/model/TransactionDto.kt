package ua.demianvolodymyr.pspdemo.model

data class TransactionDto(
    val cardNumber: String,
    val amount: Double,
    val currency: String,
    val status: String,
    val merchantId: String
)

fun TransactionDto.toEntity(): Transaction = Transaction(
    cardNumber = cardNumber,
    amount = amount,
    currency = currency,
    status = status,
    merchantId = merchantId
)
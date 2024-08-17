package ua.demianvolodymyr.pspdemo.model

data class TransactionDto(
    val cardNumber: String,
    val status: String,
    val merchantId: String
)

fun TransactionDto.toEntity(): Transaction = Transaction(
    cardNumber = cardNumber,
    status = status,
    merchantId = merchantId
)
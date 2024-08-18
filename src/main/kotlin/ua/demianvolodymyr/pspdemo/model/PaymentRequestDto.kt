package ua.demianvolodymyr.pspdemo.model

data class PaymentRequestDto(
    val cardNumber: String,
    val expiryDate: String,
    val cvv: String,
    val amount: Double,
    val currency: String,
    val merchantId: String
)

fun PaymentRequestDto.toEntity(): PaymentRequest = PaymentRequest(
    cardNumber = cardNumber,
    expiryDate = expiryDate,
    cvv = cvv,
    amount = amount,
    currency = currency,
    merchantId = merchantId
)
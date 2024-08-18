package ua.demianvolodymyr.pspdemo.router

import com.ninjasquad.springmockk.MockkBean
import io.mockk.coEvery
import io.mockk.every
import io.mockk.slot
import kotlinx.coroutines.flow.flow
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.test.web.reactive.server.expectBody
import org.springframework.test.web.reactive.server.expectBodyList
import org.springframework.web.reactive.function.BodyInserters.fromValue
import ua.demianvolodymyr.pspdemo.controller.PaymentController
import ua.demianvolodymyr.pspdemo.controller.TransactionController
import ua.demianvolodymyr.pspdemo.model.*
import ua.demianvolodymyr.pspdemo.repository.PaymentRequestRepository
import ua.demianvolodymyr.pspdemo.repository.TransactionRepository
import ua.demianvolodymyr.pspdemo.services.PaymentService

@WebFluxTest
@AutoConfigureWebTestClient
@Import(RouterConfiguration::class, PaymentController::class, TransactionController::class)
class MockedRepositoryIntegrationTest @Autowired constructor(
    @Autowired private val client: WebTestClient,

    ) {
    @MockkBean
    private lateinit var repository: PaymentRequestRepository

    @MockkBean
    private lateinit var trxRepository: TransactionRepository

    @MockkBean
    private lateinit var paymentService: PaymentService


    private fun aPaymentRequest(
        cvv: String = "543",
        cardNumber: String = "4242424242424242",
        expiryDate: String = "07/33",
        merchantId: String = "45723456",
        amount: Double = 20.0,
        currency: String = "USD"
    ) = PaymentRequest(
        cvv = cvv,
        cardNumber = cardNumber,
        expiryDate = expiryDate,
        merchantId = merchantId,
        amount = amount,
        currency = currency
    )

    private fun anotherPaymentRequest(
        cvv: String = "043",
        cardNumber: String = "5293663535991228",
        expiryDate: String = "10/31",
        amount: Double = 10.0,
        currency: String = "USD",
        merchantId: String = "87682352"
    ) = aPaymentRequest(
        cvv = cvv,
        cardNumber = cardNumber,
        expiryDate = expiryDate,
        merchantId = merchantId,
        amount = amount,
        currency = currency
    )


    private fun wrongPaymentRequest(
        cardNumber: String = "",
        cvv: String = "043",
        expiryDate: String = "01/00",
        merchantId: String = "56874356",
        amount: Double = 10.0,
        currency: String = "USD"
    ) = aPaymentRequest(
        cvv = cvv,
        cardNumber = cardNumber,
        expiryDate = expiryDate,
        merchantId = merchantId,
        amount = amount,
        currency = currency
    )

    @Test
    fun `Retrieve all payment-request`() {
        every { repository.findAll() } returns flow {
            emit(aPaymentRequest())
            emit(anotherPaymentRequest())
        }

        client.get().uri("/api/payment-request").exchange().expectStatus().isOk.expectBodyList<PaymentRequestDto>()
            .hasSize(2).contains(aPaymentRequest().toDto(), anotherPaymentRequest().toDto())
    }


    @Test
    fun `Retrieve PaymentRequest by existing id`() {

        coEvery {
            repository.findById(any())
        } coAnswers {
            aPaymentRequest()
        }
        client.get().uri("/api/payment-request/0").exchange().expectStatus().isOk.expectBody<PaymentRequestDto>()
            .isEqualTo(aPaymentRequest().toDto())
    }

    @Test
    fun `Retrieve payment request by non-existing id`() {
        coEvery { repository.findById(any()) } returns null

        client.get().uri("/api/payment-request/2").exchange().expectStatus().isNotFound
    }

    @Test
    fun `Add a new PaymentRequest`() {

        val savedPaymentRequest = slot<PaymentRequest>()
        val trx = slot<Transaction>()
        coEvery {
            repository.save(capture(savedPaymentRequest))
        } coAnswers {
            savedPaymentRequest.captured
        }

        coEvery {
            paymentService.processPayment(capture(savedPaymentRequest))
        } coAnswers {
            Transaction(null, "4242424242424242", 20.0, "USD", "Approved", "")
        }

        coEvery {
            trxRepository.save(capture(trx))
        } coAnswers {
            trx.captured
        }
        client.post().uri("/api/payment-request").accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON).bodyValue(aPaymentRequest().toDto()).exchange()
            .expectStatus().isOk.expectBody<String>()
    }

    @Test
    fun `Add a new wrong PaymentRequest`() {

        val savedPaymentRequest = slot<PaymentRequest>()
        val trx = slot<Transaction>()
        coEvery {
            repository.save(capture(savedPaymentRequest))
        } coAnswers {
            savedPaymentRequest.captured
        }

        coEvery {
            paymentService.processPayment(any())
        } coAnswers {
            throw Exception("Invalid Card Number")
        }

        coEvery {
            trxRepository.save(capture(trx))
        } coAnswers {
            trx.captured
        }
        client.post().uri("/api/payment-request").accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON).bodyValue(wrongPaymentRequest().toDto()).exchange()
            .expectStatus().isBadRequest
    }

    @Test
    fun `Add a new PaymentRequest with empty request body`() {


        val savedPaymentRequest = slot<PaymentRequest>()
        coEvery {
            repository.save(capture(savedPaymentRequest))
        } coAnswers {
            savedPaymentRequest.captured
        }


        client.post().uri("/api/payment-request").accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON).body(fromValue("{}")).exchange().expectStatus().isBadRequest
    }

}

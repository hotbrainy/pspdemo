package ua.demianvolodymyr.pspdemo.controller

//import io.klogging.Klogging
import kotlinx.coroutines.flow.map

import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.awaitBodyOrNull
import org.springframework.web.reactive.function.server.bodyAndAwait
import org.springframework.web.reactive.function.server.bodyValueAndAwait
import org.springframework.web.reactive.function.server.buildAndAwait
import ua.demianvolodymyr.pspdemo.model.*
import ua.demianvolodymyr.pspdemo.repository.PaymentRequestRepository
import ua.demianvolodymyr.pspdemo.repository.TransactionRepository
import ua.demianvolodymyr.pspdemo.services.PaymentService

@Component
class PaymentController(
    private val paymentRequestRepository: PaymentRequestRepository,
    private val transactionRepository: TransactionRepository,
    private val paymentService: PaymentService
) {


    suspend fun getAll(req: ServerRequest): ServerResponse {
        return ServerResponse
            .ok()
            .contentType(MediaType.APPLICATION_JSON)
            .bodyAndAwait(
                paymentRequestRepository.findAll().map { it.toDto() }
            )
    }

    suspend fun getById(req: ServerRequest): ServerResponse {
        val id = Integer.parseInt(req.pathVariable("id"))
        val existingPaymentRequest = paymentRequestRepository.findById(id.toLong())

        return existingPaymentRequest?.let {
            ServerResponse
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValueAndAwait(it)
        } ?: ServerResponse.notFound().buildAndAwait()
    }

    suspend fun add(req: ServerRequest): ServerResponse {
        try {
            val receivedPaymentRequest = req.awaitBodyOrNull(PaymentRequestDto::class)!!.toEntity()

            return receivedPaymentRequest?.let {
                val trxId = paymentService.processPayment(receivedPaymentRequest)
                receivedPaymentRequest.merchantId = trxId
                ServerResponse
                    .ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValueAndAwait(
                        paymentRequestRepository
                            .save(receivedPaymentRequest)
                            .toDto()
                    )
            } ?: ServerResponse.badRequest().bodyValueAndAwait(mapOf("message" to "Error", "success" to false))
        }catch (e:Exception){
            return ServerResponse.badRequest().bodyValueAndAwait(mapOf("message" to e.message, "success" to false))
        }
    }

    suspend fun add2(req: ServerRequest): ServerResponse {
        try {
            val receivedPaymentRequest = req.awaitBodyOrNull(PaymentRequestDto::class)!!.toEntity()
            val trxID = paymentService.processPayment(receivedPaymentRequest)
            receivedPaymentRequest.merchantId = trxID
            paymentRequestRepository
                .save(receivedPaymentRequest)
                .toDto()
            return receivedPaymentRequest?.let {
                ServerResponse
                    .ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValueAndAwait(
//                        transactionRepository.save(it).toDto()
                        ""
                    )
            } ?: ServerResponse.badRequest().bodyValueAndAwait(mapOf("message" to "Error", "success" to false))
        } catch (e: Exception) {
            return ServerResponse.badRequest().bodyValueAndAwait(mapOf("message" to e.message, "success" to false))
        }
    }

    suspend fun update(req: ServerRequest): ServerResponse {
        val id = req.pathVariable("id")

        val receivedPaymentRequest = req.awaitBodyOrNull(PaymentRequestDto::class)
            ?: return ServerResponse.badRequest().buildAndAwait()

        val existingPaymentRequest = paymentRequestRepository.findById(id.toLong())
            ?: return ServerResponse.notFound().buildAndAwait()

        return ServerResponse
            .ok()
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValueAndAwait(
                paymentRequestRepository.save(
                    receivedPaymentRequest.toEntity().copy(id = existingPaymentRequest.id)
                ).toDto()
            )
    }

    suspend fun delete(req: ServerRequest): ServerResponse {
        val id = req.pathVariable("id")

        return if (paymentRequestRepository.existsById(id.toLong())) {
            paymentRequestRepository.deleteById(id.toLong())
            ServerResponse.noContent().buildAndAwait()
        } else {
            ServerResponse.notFound().buildAndAwait()
        }
    }
}
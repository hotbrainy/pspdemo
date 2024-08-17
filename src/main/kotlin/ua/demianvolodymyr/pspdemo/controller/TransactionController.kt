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
import ua.demianvolodymyr.pspdemo.model.PaymentRequestDto
import ua.demianvolodymyr.pspdemo.model.toDto
import ua.demianvolodymyr.pspdemo.model.toEntity
import ua.demianvolodymyr.pspdemo.repository.PaymentRequestRepository
import ua.demianvolodymyr.pspdemo.repository.TransactionRepository
import ua.demianvolodymyr.pspdemo.services.PaymentService

@Component
class TransactionController(
    private val transactionRepository: TransactionRepository
)  {


    suspend fun getAll(req: ServerRequest): ServerResponse {
        return ServerResponse
            .ok()
            .contentType(MediaType.APPLICATION_JSON)
            .bodyAndAwait(
                transactionRepository.findAll().map { it.toDto() }
            )
    }

    suspend fun getById(req: ServerRequest): ServerResponse {
        val id = Integer.parseInt(req.pathVariable("id"))
        val existingTransaction= transactionRepository.findById(id.toLong())

        return existingTransaction?.let {
            ServerResponse
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValueAndAwait(it)
        } ?: ServerResponse.notFound().buildAndAwait()
    }
}
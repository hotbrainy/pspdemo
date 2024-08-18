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
) {
    /**
     * Retrieves all transactions.
     *
     * This function fetches all transactions from the database and returns them as a list of DTOs
     * in a JSON response. It handles the request asynchronously and ensures that the response
     * is formatted as JSON.
     *
     * @param req The `ServerRequest` containing the HTTP request data. No specific parameters are expected.
     *
     * @return `ServerResponse`
     *         - On success: A JSON response (HTTP 200) containing a list of all transactions.
     */

    suspend fun getAll(req: ServerRequest): ServerResponse {
        return ServerResponse
            .ok()
            .contentType(MediaType.APPLICATION_JSON)
            .bodyAndAwait(
                transactionRepository.findAll().map { it.toDto() }
            )
    }

    /**
     * Retrieves a transaction by its ID.
     *
     * This function fetches a transaction from the database based on the provided ID.
     * If the transaction is found, it is returned as a JSON response. If not,
     * a `notFound` response is returned.
     *
     * @param req The `ServerRequest` containing the HTTP request data. It is expected to have a
     *            path variable "id" representing the unique identifier of the transaction to be retrieved.
     *
     * @return `ServerResponse`
     *         - On success: A JSON response (HTTP 200) containing the transaction data.
     *         - On failure: A `notFound` response (HTTP 404) if the transaction with the specified ID is not found.
     */

    suspend fun getById(req: ServerRequest): ServerResponse {
        val id = Integer.parseInt(req.pathVariable("id"))

        // Find the transaction in the repository using the provided ID.
        val existingTransaction = transactionRepository.findById(id.toLong())

        return existingTransaction?.let {
            ServerResponse
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValueAndAwait(it)
        } ?: ServerResponse.notFound().buildAndAwait()
    }
}
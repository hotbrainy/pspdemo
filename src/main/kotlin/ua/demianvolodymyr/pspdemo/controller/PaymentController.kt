package ua.demianvolodymyr.pspdemo.controller

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
import ua.demianvolodymyr.pspdemo.services.PaymentService

@Component
class PaymentController(
    private val paymentRequestRepository: PaymentRequestRepository,
    private val paymentService: PaymentService
) {
    /**
     * Retrieves all payment requests.
     *
     * This function fetches all payment requests from the database and returns them as a list of DTOs
     * in a JSON response. It handles the request asynchronously and ensures that the response is
     * formatted as JSON.
     *
     * @param req The `ServerRequest` containing the HTTP request data. No specific parameters are expected.
     *
     * @return `ServerResponse`
     *         - On success: A JSON response (HTTP 200) containing a list of all payment requests.
     */

    suspend fun getAll(req: ServerRequest): ServerResponse {
        return ServerResponse
            .ok()
            .contentType(MediaType.APPLICATION_JSON)
            .bodyAndAwait(
                paymentRequestRepository.findAll().map { it.toDto() }
            )
    }

    /**
     * Retrieves a payment request by its ID.
     *
     * This function fetches a payment request from the database based on the provided ID.
     * If the payment request is found, it is returned as a JSON response. If not,
     * a `notFound` response is returned.
     *
     * @param req The `ServerRequest` containing the HTTP request data. It is expected to have a
     *            path variable "id" representing the unique identifier of the payment request to be retrieved.
     *
     * @return `ServerResponse`
     *         - On success: A JSON response (HTTP 200) containing the payment request data.
     *         - On failure: A `notFound` response (HTTP 404) if the payment request with the specified ID is not found.
     */
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

    /**
     * Handles the processing of a payment request.
     *
     * This function processes a payment request received via an HTTP POST request,
     * validates and processes the payment, assigns a transaction ID, saves the payment request
     * in the repository, and returns a JSON response. If any error occurs during processing,
     * a bad request response is returned with an appropriate error message.
     *
     * @param req The `ServerRequest` containing the HTTP request data. It is expected to have a
     *            JSON body that can be deserialized into a `PaymentRequestDto`.
     *            - The request body should contain fields necessary for payment processing,
     *              such as card details, amount, currency, etc.
     *
     * @return `ServerResponse`
     *         - On success: A JSON response containing the saved payment request along with its
     *           newly assigned transaction ID and HTTP 200 status.
     *         - On failure: A JSON response with an error message and HTTP 400 status.
     */
    suspend fun add(req: ServerRequest): ServerResponse {
        try {
            val receivedPaymentRequest = req.awaitBodyOrNull(PaymentRequestDto::class)!!.toEntity()

            // Process the payment and get a transaction ID.
            val trx: Transaction = paymentService.processPayment(receivedPaymentRequest)
            return receivedPaymentRequest.let {
                paymentRequestRepository
                    .save(receivedPaymentRequest)
                    .toDto()
                ServerResponse
                    .ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValueAndAwait(
                        trx
                    )
            }
        } catch (e: Exception) {
            // If any exception occurs, return a bad request response with an error message.
            return ServerResponse.badRequest().bodyValueAndAwait(mapOf("message" to e.message, "success" to false))
        }
    }

    /**
     * Handles the update of an existing payment request.
     *
     * This function updates an existing payment request identified by an ID. It retrieves the
     * payment request from the database, applies the new data from the request body, and saves
     * the updated request. If the payment request is not found or the request body is invalid,
     * appropriate error responses are returned.
     *
     * @param req The `ServerRequest` containing the HTTP request data. It is expected to have a
     *            path variable "id" representing the payment request's unique identifier,
     *            and a JSON body that can be deserialized into a `PaymentRequestDto`.
     *            - The request body should include updated payment details.
     *
     * @return `ServerResponse`
     *         - On success: A JSON response containing the updated payment request and HTTP 200 status.
     *         - On failure: A `badRequest` response (HTTP 400) if the body is null, or a `notFound`
     *           response (HTTP 404) if the payment request with the specified ID is not found.
     */
    suspend fun update(req: ServerRequest): ServerResponse {

        // Extract the payment request ID from the path variable "id".
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

    /**
     * Handles the deletion of an existing payment request.
     *
     * This function deletes a payment request identified by an ID from the database.
     * If the payment request is found, it is deleted, and a `noContent` response is returned.
     * If the payment request is not found, a `notFound` response is returned.
     *
     * @param req The `ServerRequest` containing the HTTP request data. It is expected to have a
     *            path variable "id" representing the unique identifier of the payment request
     *            to be deleted.
     *
     * @return `ServerResponse`
     *         - On success: A `noContent` response (HTTP 204) indicating the payment request was successfully deleted.
     *         - On failure: A `notFound` response (HTTP 404) if the payment request with the specified ID is not found.
     */
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
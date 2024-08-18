package ua.demianvolodymyr.pspdemo.router

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import ua.demianvolodymyr.pspdemo.controller.PaymentController
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.web.reactive.function.server.coRouter
import ua.demianvolodymyr.pspdemo.controller.TransactionController

/**
 * Configures the routing for the API endpoints.
 *
 * The `RouterConfiguration` class sets up the routing for payment request and transaction
 * endpoints using the Spring WebFlux `coRouter` function. It defines the paths and the
 * corresponding controller methods for handling HTTP requests.
 *
 * @property paymentController The controller handling payment request-related endpoints.
 * @property transactionController The controller handling transaction-related endpoints.
 */

@Configuration
class RouterConfiguration(
    private val paymentController: PaymentController,
    private val transactionController: TransactionController
) {

    /**
     * Configures the API routing.
     *
     * Defines the routes for payment requests and transactions, including the paths, HTTP methods,
     * and the corresponding controller methods.
     *
     * @return A `RouterFunction` that maps the API endpoints to their respective controller methods.
     */
    @Bean
    fun apiRouter() = coRouter {
        // Define routes for payment request endpoints.
        "/api/payment-request".nest {
            accept(APPLICATION_JSON).nest {
                // Route for retrieving all payment requests.
                GET("", paymentController::getAll)

                contentType(APPLICATION_JSON).nest {
                    // Route for adding a new payment request.
                    POST("", paymentController::add)
                }

                // Routes for retrieving a specific payment request by ID.
                "/{id}".nest {
                    GET("", paymentController::getById)
                }
            }
        }


        // Define routes for transaction endpoints.
        "/api/transactions".nest {
            accept(APPLICATION_JSON).nest {
                // Route for retrieving all transactions.
                GET("", transactionController::getAll)

                "/{id}".nest {
                    // Routes for retrieving a specific transaction by ID.
                    GET("", transactionController::getById)
                }
            }
        }
    }
}
  
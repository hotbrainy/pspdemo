package ua.demianvolodymyr.pspdemo.router

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import ua.demianvolodymyr.pspdemo.controller.PaymentController
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.web.reactive.function.server.coRouter
import ua.demianvolodymyr.pspdemo.controller.TransactionController

@Configuration
class RouterConfiguration(
    private val paymentController: PaymentController,
    private val transactionController: TransactionController
) {
    @Bean
    fun apiRouter() = coRouter {
        "/api/payment-request".nest {
            accept(APPLICATION_JSON).nest {
                GET("", paymentController::getAll)

                contentType(APPLICATION_JSON).nest {
                    POST("", paymentController::add)
                }

                "/{id}".nest {
                    GET("", paymentController::getById)
                }
            }
        }


        "/api/transactions".nest {
            accept(APPLICATION_JSON).nest {
                GET("", transactionController::getAll)

                "/{id}".nest {
                    GET("", transactionController::getById)
                }
            }
        }
    }
}
  
package ua.demianvolodymyr.pspdemo.repository

import kotlinx.coroutines.flow.Flow
import ua.demianvolodymyr.pspdemo.model.PaymentRequest
import org.springframework.data.repository.kotlin.CoroutineCrudRepository


interface PaymentRequestRepository : CoroutineCrudRepository<PaymentRequest, Long> {
    override fun findAll(): Flow<PaymentRequest>
    override suspend fun findById(id: Long): PaymentRequest?
    override suspend fun existsById(id: Long): Boolean
    override suspend fun <S : PaymentRequest> save(entity: S): PaymentRequest
    override suspend fun deleteById(id: Long)
}
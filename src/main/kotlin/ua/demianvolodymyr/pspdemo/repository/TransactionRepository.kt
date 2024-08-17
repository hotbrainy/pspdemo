package ua.demianvolodymyr.pspdemo.repository

import kotlinx.coroutines.flow.Flow
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import ua.demianvolodymyr.pspdemo.model.Transaction


interface TransactionRepository : CoroutineCrudRepository<Transaction, Long> {
    override fun findAll(): Flow<Transaction>
    override suspend fun findById(id: Long): Transaction?
    override suspend fun existsById(id: Long): Boolean
    override suspend fun <S : Transaction> save(entity: S): Transaction
    override suspend fun deleteById(id: Long)
}
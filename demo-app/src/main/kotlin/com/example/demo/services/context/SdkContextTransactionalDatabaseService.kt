package com.example.demo.services.context

import com.example.demo.jooq.generated.tables.pojos.SdkContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.jooq.DSLContext
import org.jooq.kotlin.coroutines.transactionCoroutine
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.transaction.reactive.TransactionalOperator
import reactor.core.publisher.Flux
import reactor.kotlin.core.publisher.toFlux

@Service
class SdkContextTransactionalDatabaseService(
    private val repository: SdkContextRepository,
    private val dao: SdkContextDao,
    private val transactionalOperator: TransactionalOperator,
    private val dslContext: DSLContext
) {
    fun saveAllRepNonTx(contexts: List<SdkContextDto>): Flux<SdkContextDto> =
        contexts.toFlux().concatMap { repository.save(it) }

    @Transactional
    fun saveAllRepSpringTx(contexts: List<SdkContextDto>): Flux<SdkContextDto> =
        contexts.toFlux().concatMap { repository.save(it) }

    fun saveAllRepReactorTx(contexts: List<SdkContextDto>): Flux<SdkContextDto> =
        contexts.toFlux().concatMap { repository.save(it) }
            .`as` { transactionalOperator.transactional(it) }

    fun saveAllDaoNonTx(contexts: List<SdkContext>): Flux<SdkContext> =
        contexts.toFlux().concatMap { dao.upsert(it) }

    @Transactional
    fun saveAllDaoSpringTx(contexts: List<SdkContext>): Flux<SdkContext> =
        contexts.toFlux().concatMap { dao.upsert(it) }

    fun saveAllDaoReactorTx(contexts: List<SdkContext>): Flux<SdkContext> =
        contexts.toFlux().concatMap { dao.upsert(it) }
            .`as` { transactionalOperator.transactional(it) }

    fun saveAllDaoJooqTx(contexts: List<SdkContext>): Flux<SdkContext> =
        dslContext.transactionPublisher { cfg ->
            val trxDao = SdkContextDaoImpl(cfg.dsl())
            contexts.toFlux().concatMap { trxDao.upsert(it) }
        }.let { Flux.from(it) }

    suspend fun saveAllDaoJooqTxCor(contexts: List<SdkContext>): List<SdkContext> =
        dslContext.transactionCoroutine { cfg ->
            val trxDao = SdkContextDaoImpl(cfg.dsl())
            contexts.map { trxDao.upsertCor(it) }
        }

    fun saveAllDaoJooqTxCorFlow(contexts: List<SdkContext>): Flow<SdkContext> =
        flow {
            dslContext.transactionCoroutine { cfg ->
                val trxDao = SdkContextDaoImpl(cfg.dsl())
                contexts.forEach { context ->
                    emit(trxDao.upsertCor(context))
                }
            }
        }
}

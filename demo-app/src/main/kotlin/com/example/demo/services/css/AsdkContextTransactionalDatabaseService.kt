package com.example.demo.services.css

import com.example.demo.jooq.generated.tables.pojos.AsdkContext
import org.jooq.DSLContext
import org.jooq.kotlin.coroutines.transactionCoroutine
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.transaction.reactive.TransactionalOperator
import reactor.core.publisher.Flux
import reactor.kotlin.core.publisher.toFlux

@Service
class AsdkContextTransactionalDatabaseService(
    private val repository: AsdkContextRepository,
    private val dao: AsdkContextDao,
    private val transactionalOperator: TransactionalOperator,
    private val dslContext: DSLContext
) {
    fun saveAllRepNonTx(contexts: List<AsdkContextDto>): Flux<AsdkContextDto> {
        return contexts.toFlux()
            .concatMap { repository.save(it) }
    }

    @Transactional
    fun saveAllRepSpringTx(contexts: List<AsdkContextDto>): Flux<AsdkContextDto> {
        return contexts.toFlux()
            .concatMap { repository.save(it) }
    }

    fun saveAllRepReactorTx(contexts: List<AsdkContextDto>): Flux<AsdkContextDto> {
        return contexts.toFlux()
            .concatMap { repository.save(it) }
            .`as` { transactionalOperator.transactional(it) }
    }

    fun saveAllDaoNonTx(contexts: List<AsdkContext>): Flux<AsdkContext> {
        return contexts.toFlux()
            .concatMap { dao.upsert(it) }
    }

    @Transactional
    fun saveAllDaoSpringTx(contexts: List<AsdkContext>): Flux<AsdkContext> {
        return contexts.toFlux()
            .concatMap { dao.upsert(it) }
    }

    fun saveAllDaoReactorTx(contexts: List<AsdkContext>): Flux<AsdkContext> {
        return contexts.toFlux()
            .concatMap { dao.upsert(it) }
            .`as` { transactionalOperator.transactional(it) }
    }

    fun saveAllDaoJooqTx(contexts: List<AsdkContext>): Flux<AsdkContext> {
        return dslContext.transactionPublisher { cfg ->
            val trxDao = AsdkContextDaoImpl(cfg.dsl())
            contexts.toFlux()
                .concatMap { trxDao.upsert(it) }
        }.let { Flux.from(it) }
    }

    suspend fun saveAllDaoJooqTxCor(contexts: List<AsdkContext>): List<AsdkContext> {
        return dslContext.transactionCoroutine { cfg ->
            val trxDao = AsdkContextDaoImpl(cfg.dsl())
            contexts.map { trxDao.upsertCor(it) }
        }
    }
}

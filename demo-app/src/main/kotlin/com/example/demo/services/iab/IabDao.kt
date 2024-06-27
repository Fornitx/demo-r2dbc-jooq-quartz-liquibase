package com.example.demo.services.iab

import com.example.demo.jooq.tables.pojos.IabMethod
import com.example.demo.jooq.tables.pojos.IabService
import com.example.demo.jooq.tables.references.IAB_METHOD
import com.example.demo.jooq.tables.references.IAB_SERVICE
import kotlinx.coroutines.reactor.awaitSingle
import org.jooq.DSLContext
import reactor.core.publisher.Flux

class IabDao(private val dslContext: DSLContext) {
    suspend fun findServices(): List<IabService> {
        return dslContext.selectFrom(IAB_SERVICE)
            .let { Flux.from(it) }
            .map { it.into(IabService::class.java) }
            .collectList()
            .awaitSingle()
    }

    suspend fun findMethods(): List<IabMethod> {
        return dslContext.selectFrom(IAB_METHOD)
            .let { Flux.from(it) }
            .map { it.into(IabMethod::class.java) }
            .collectList()
            .awaitSingle()
    }
}

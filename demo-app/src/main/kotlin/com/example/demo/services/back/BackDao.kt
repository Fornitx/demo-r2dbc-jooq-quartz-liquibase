package com.example.demo.services.back

import com.example.demo.jooq.generated.tables.pojos.BackMethod
import com.example.demo.jooq.generated.tables.pojos.BackService
import com.example.demo.jooq.generated.tables.references.BACK_METHOD
import com.example.demo.jooq.generated.tables.references.BACK_SERVICE
import kotlinx.coroutines.reactor.awaitSingle
import org.jooq.DSLContext
import reactor.core.publisher.Flux

class BackDao(private val dslContext: DSLContext) {
    suspend fun findServices(): List<BackService> {
        return dslContext.selectFrom(BACK_SERVICE)
            .let { Flux.from(it) }
            .map { it.into(BackService::class.java) }
            .collectList()
            .awaitSingle()
    }

    suspend fun findMethods(): List<BackMethod> {
        return dslContext.selectFrom(BACK_METHOD)
            .let { Flux.from(it) }
            .map { it.into(BackMethod::class.java) }
            .collectList()
            .awaitSingle()
    }
}

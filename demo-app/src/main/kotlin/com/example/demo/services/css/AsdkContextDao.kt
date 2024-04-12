package com.example.demo.services.css

import com.example.demo.services.css.page.Page
import com.example.demo.services.css.page.Slice
import com.example.demo.jooq.generated.tables.pojos.AsdkContext
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.*

interface AsdkContextDao {
    fun count(): Mono<Int>

    fun findAll(): Flux<AsdkContext>

    fun findAll(page: Page): Mono<Slice<AsdkContext>>

    fun findById(id: UUID): Mono<AsdkContext>

    fun upsert(context: AsdkContext): Mono<AsdkContext>

    suspend fun upsertCor(context: AsdkContext): AsdkContext

    fun delete(id: UUID, version: Int): Mono<Int>

    fun deleteAll(): Mono<Int>

    fun deleteAll(ids: Map<UUID, Int>): Flux<UUID>
}

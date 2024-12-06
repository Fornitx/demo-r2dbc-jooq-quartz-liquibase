package com.example.demo.services.context

import com.example.demo.jooq.generated.tables.pojos.SdkContext
import com.example.demo.services.context.page.Page
import com.example.demo.services.context.page.Slice
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.*

interface SdkContextDao {
    fun count(): Mono<Int>

    fun findAll(): Flux<SdkContext>

    fun findAll(page: Page): Mono<Slice<SdkContext>>

    fun findById(id: UUID): Mono<SdkContext>

    fun upsert(context: SdkContext): Mono<SdkContext>

    suspend fun upsertCor(context: SdkContext): SdkContext

    fun delete(id: UUID, version: Int): Mono<Int>

    fun deleteAll(): Mono<Int>

    fun deleteAll(ids: Map<UUID, Int>): Flux<UUID>
}

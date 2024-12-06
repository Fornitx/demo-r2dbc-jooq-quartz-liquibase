package com.example.demo.services.context

import com.example.demo.jooq.generated.tables.pojos.SdkContext
import com.example.demo.jooq.generated.tables.references.SDK_CONTEXT
import com.example.demo.services.context.page.Page
import com.example.demo.services.context.page.Slice
import kotlinx.coroutines.reactor.awaitSingle
import org.jooq.DSLContext
import org.jooq.Field
import org.jooq.Record
import org.jooq.impl.DSL
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.*
import java.util.concurrent.atomic.AtomicInteger

class SdkContextDaoImpl(private val dslContext: DSLContext) : SdkContextDao {
    private val fieldsPlusCount: Collection<Field<*>> by lazy {
        SDK_CONTEXT.fields().toList() + DSL.count().over()
    }

    override fun count(): Mono<Int> {
        return dslContext.selectCount()
            .from(SDK_CONTEXT)
            .let { Mono.from(it) }
            .map { it.value1() }
    }

    override fun findAll(): Flux<SdkContext> {
        return dslContext.selectFrom(SDK_CONTEXT)
            .fetchSize(100)
            .let { Flux.from(it) }
            .map { e -> e.into(SdkContext::class.java) }
    }

    override fun findAll(page: Page): Mono<Slice<SdkContext>> {
        val count = AtomicInteger()
        return dslContext.select(fieldsPlusCount)
            .from(SDK_CONTEXT)
            .limit(page.limit)
            .offset(page.offset)
            .let { Flux.from(it) }
            .map {
                count.set(it[it.size() - 1] as Int)
                it.into(*SDK_CONTEXT.fields()).into(SdkContext::class.java)
            }
            .collectList()
            .map { Slice(it, count.get()) }
    }

    override fun findById(id: UUID): Mono<SdkContext> {
        return dslContext.selectFrom(SDK_CONTEXT)
            .where(SDK_CONTEXT.ID.eq(id))
            .let { Mono.from(it) }
            .map { it.into(SdkContext::class.java) }
    }

    override fun upsert(context: SdkContext): Mono<SdkContext> {
        if (context.id == null) {
            return dslContext.insertInto(SDK_CONTEXT)
                .columns(
                    SDK_CONTEXT.MSG,
                    SDK_CONTEXT.STATUS,
                    SDK_CONTEXT.RULES,
                    SDK_CONTEXT.CONFIG
                )
                .values(context.msg, context.status, context.rules, context.config)
                .returning()
                .let { Mono.from(it) }
                .map { e: Record ->
                    e.into(
                        SdkContext::class.java
                    )
                }
        } else {
            return dslContext.update(SDK_CONTEXT)
                .set(SDK_CONTEXT.MSG, context.msg)
                .set(SDK_CONTEXT.STATUS, context.status)
                .set(SDK_CONTEXT.RULES, context.rules)
                .set(SDK_CONTEXT.CONFIG, context.config)
                .set(SDK_CONTEXT.UPDATED_AT, DSL.currentOffsetDateTime())
                .set(SDK_CONTEXT.VERSION, SDK_CONTEXT.VERSION.plus(1))
                .where(
                    SDK_CONTEXT.ID.eq(context.id),
                    SDK_CONTEXT.VERSION.eq(context.version)
                )
                .returning()
                .let { Mono.from(it) }
                .map { it.into(SdkContext::class.java) }
        }
    }

    override suspend fun upsertCor(context: SdkContext): SdkContext {
        return upsert(context).awaitSingle()
    }

    override fun delete(id: UUID, version: Int): Mono<Int> {
        return dslContext.deleteFrom(SDK_CONTEXT)
            .where(
                SDK_CONTEXT.ID.eq(id),
                SDK_CONTEXT.VERSION.eq(version)
            )
            .let { Mono.from(it) }
    }

    override fun deleteAll(): Mono<Int> {
        return dslContext.deleteFrom(SDK_CONTEXT)
            .let { Mono.from(it) }
    }

    override fun deleteAll(ids: Map<UUID, Int>): Flux<UUID> {
        val conditions = ids.entries.map {
            DSL.and(
                SDK_CONTEXT.ID.eq(it.key),
                SDK_CONTEXT.VERSION.eq(it.value)
            )
        }
        return dslContext.deleteFrom(SDK_CONTEXT)
            .where(DSL.or(conditions))
            .returningResult(SDK_CONTEXT.ID)
            .let { Flux.from(it) }
            .map { it.component1() }
    }
}

package com.example.demo.services.css

import com.example.demo.services.css.page.Page
import com.example.demo.services.css.page.Slice
import com.example.demo.jooq.generated.tables.AsdkContext.Companion.ASDK_CONTEXT
import com.example.demo.jooq.generated.tables.pojos.AsdkContext
import kotlinx.coroutines.reactor.awaitSingle
import org.jooq.DSLContext
import org.jooq.Field
import org.jooq.Record
import org.jooq.impl.DSL
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.*
import java.util.concurrent.atomic.AtomicInteger

class AsdkContextDaoImpl(private val dslContext: DSLContext) : AsdkContextDao {
    private val fieldsPlusCount: Collection<Field<*>> by lazy {
        ASDK_CONTEXT.fields().toList() + DSL.count().over()
    }

    override fun count(): Mono<Int> {
        return dslContext.selectCount()
            .from(ASDK_CONTEXT)
            .let { Mono.from(it) }
            .map { it.value1() }
    }

    override fun findAll(): Flux<AsdkContext> {
        return dslContext.selectFrom(ASDK_CONTEXT)
            .fetchSize(100)
            .let { Flux.from(it) }
            .map { e -> e.into(AsdkContext::class.java) }
    }

    override fun findAll(page: Page): Mono<Slice<AsdkContext>> {
        val count = AtomicInteger()
        return dslContext.select(fieldsPlusCount)
            .from(ASDK_CONTEXT)
            .limit(page.limit)
            .offset(page.offset)
            .let { Flux.from(it) }
            .map {
                count.set(it[it.size() - 1] as Int)
                it.into(*ASDK_CONTEXT.fields()).into(AsdkContext::class.java)
            }
            .collectList()
            .map { Slice(it, count.get()) }
    }

    override fun findById(id: UUID): Mono<AsdkContext> {
        return dslContext.selectFrom(ASDK_CONTEXT)
            .where(ASDK_CONTEXT.ID.eq(id))
            .let { Mono.from(it) }
            .map { it.into(AsdkContext::class.java) }
    }

    override fun upsert(context: AsdkContext): Mono<AsdkContext> {
        if (context.id == null) {
            return dslContext.insertInto(ASDK_CONTEXT)
                .columns(
                    ASDK_CONTEXT.MSG,
                    ASDK_CONTEXT.STATUS,
                    ASDK_CONTEXT.RULES,
                    ASDK_CONTEXT.CONFIG
                )
                .values(context.msg, context.status, context.rules, context.config)
                .returning()
                .let { Mono.from(it) }
                .map { e: Record ->
                    e.into(
                        AsdkContext::class.java
                    )
                }
        } else {
            return dslContext.update(ASDK_CONTEXT)
                .set(ASDK_CONTEXT.MSG, context.msg)
                .set(ASDK_CONTEXT.STATUS, context.status)
                .set(ASDK_CONTEXT.RULES, context.rules)
                .set(ASDK_CONTEXT.CONFIG, context.config)
                .set(ASDK_CONTEXT.UPDATED_AT, DSL.currentOffsetDateTime())
                .set(ASDK_CONTEXT.VERSION, ASDK_CONTEXT.VERSION.plus(1))
                .where(
                    ASDK_CONTEXT.ID.eq(context.id),
                    ASDK_CONTEXT.VERSION.eq(context.version)
                )
                .returning()
                .let { Mono.from(it) }
                .map { it.into(AsdkContext::class.java) }
        }
    }

    override suspend fun upsertCor(context: AsdkContext): AsdkContext {
        return upsert(context).awaitSingle()
    }

    override fun delete(id: UUID, version: Int): Mono<Int> {
        return dslContext.deleteFrom(ASDK_CONTEXT)
            .where(
                ASDK_CONTEXT.ID.eq(id),
                ASDK_CONTEXT.VERSION.eq(version)
            )
            .let { Mono.from(it) }
    }

    override fun deleteAll(): Mono<Int> {
        return dslContext.deleteFrom(ASDK_CONTEXT)
            .let { Mono.from(it) }
    }

    override fun deleteAll(ids: Map<UUID, Int>): Flux<UUID> {
        val conditions = ids.entries.map {
            DSL.and(
                ASDK_CONTEXT.ID.eq(it.key),
                ASDK_CONTEXT.VERSION.eq(it.value)
            )
        }
        return dslContext.deleteFrom(ASDK_CONTEXT)
            .where(DSL.or(conditions))
            .returningResult(ASDK_CONTEXT.ID)
            .let { Flux.from(it) }
            .map { it.component1() }
    }
}

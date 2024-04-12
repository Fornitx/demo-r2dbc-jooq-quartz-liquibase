package com.example.demo.services.css;

import com.example.demo.AbstractDatabaseTest;
import com.example.demo.services.css.page.Page;
import com.example.demo.jooq.generated.tables.pojos.AsdkContext;
import lombok.extern.slf4j.Slf4j;
import org.jooq.JSONB;
import org.jooq.impl.DSL;
import org.jooq.impl.SQLDataType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Set;

import static com.example.demo.jooq.generated.Tables.ASDK_CONTEXT;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@Slf4j
class AsdkContextDaoTest extends AbstractDatabaseTest {
    @Autowired
    private AsdkContextDao dao;

    @AfterEach
    void cleanUp() {
//        dao.deleteAll().block();
    }

    @Test
    void test() {
        var context = new AsdkContext(null,
            null,
            null,
            null,
            "123",
            StatusEnum.ONE,
            new RuleDto(true, Set.of()),
            JSONB.jsonb("{\"a\": \"b\"}"));

        var saved = dao.upsert(context).block();
        log.info("saved = {}", saved);
        assertThat(saved).usingRecursiveComparison()
            .ignoringExpectedNullFields()
            .isEqualTo(context);

        assertThat(dao.findById(saved.id()).block()).isEqualTo(saved);
        assertThat(dao.findAll().single().block()).isEqualTo(saved);
    }

    @Test
    void testMany() {
        var saved = Flux.<AsdkContext>generate(sink -> {
                var context = new AsdkContext(null,
                    null,
                    null,
                    null,
                    "123",
                    StatusEnum.ONE,
                    new RuleDto(true, Set.of()),
                    JSONB.jsonb("{}"));

                sink.next(context);
            })
            .take(5)
            .concatMap(e -> dao.upsert(e))
            .collectList()
            .block();

        log.info("saved = {}", saved);
        assertThat(saved).hasSize(5);

        var findAll = dao.findAll(new Page(1, 1)).block();
        log.info("findAll = {}", findAll);

        assertThat(findAll.rows).hasSize(1);
        assertThat(findAll.count).isEqualTo(5);
    }

    @Test
    void testSuperMany() {
//        Flux.<AsdkContext>generate(sink -> {
//                var context = new AsdkContext(null,
//                    null,
//                    null,
//                    null,
//                    "123",
//                    StatusEnum.ONE,
//                    new RuleDto(true, Set.of()),
//                    JSONB.jsonb("{}"));
//
//                sink.next(context);
//            })
//            .take(10_000)
//            .concatMap(e -> dao.upsert(e))
//            .blockLast();

        var findAll = dao.findAll(new Page(1, 1)).block();
        log.info("findAll = {}", findAll);

        assertThat(findAll.rows).hasSize(1);
        assertThat(findAll.count).isEqualTo(10_000);

        dao.findAll()
            .doOnNext(e -> System.out.print("1"))
            .as(StepVerifier::create)
            .expectNextCount(10_000)
            .verifyComplete();
    }

    @Test
    void testMiniDao() {
        var query = dslContext.insertInto(DSL.table("asdk_context"))
            .columns(
                DSL.field("msg", SQLDataType.VARCHAR),
                DSL.field("status", SQLDataType.VARCHAR),
                DSL.field("rules", SQLDataType.JSONB),
                DSL.field("config", SQLDataType.JSONB)
            )
            .values("123", StatusEnum.TWO.toString(), JSONB.jsonb("{}"), JSONB.jsonb("{}"))
            .returning(ASDK_CONTEXT.fields());

        var asdkContext = Mono.from(query)
            .map(e -> {
                log.info("record = {}", e);
                return e;
            })
            .map(e -> e.into(AsdkContext.class))
            .block();

        log.info("asdkContext = {}", asdkContext);

        assertNotNull(asdkContext);
        assertEquals("123", asdkContext.msg());
    }
}

package com.example.demo.services.context

import com.example.demo.AbstractDatabaseTest
import com.example.demo.jooq.generated.tables.pojos.SdkContext
import kotlinx.coroutines.runBlocking
import org.jooq.exception.DataException
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.MethodOrderer
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestMethodOrder
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.r2dbc.BadSqlGrammarException
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
class TransactionalTest : AbstractDatabaseTest() {
    @Autowired
    private lateinit var repository: SdkContextRepository

    @Autowired
    private lateinit var dao: SdkContextDao

    @Autowired
    private lateinit var service: SdkContextTransactionalDatabaseService

    @AfterEach
    fun cleanUp() {
        repository.deleteAll().block()
    }

    @Test
    @Order(1)
    fun saveAllRepNonTx() {
        val ex = assertFailsWith<BadSqlGrammarException> {
            service.saveAllRepNonTx(
                listOf(
                    newAsdkContextDto("123"),
                    newAsdkContextDto("1".repeat(100))
                )
            ).blockLast()
        }
        log.error(ex) {}

        assertEquals(1, repository.count().block())
    }

    @Test
    @Order(2)
    fun saveAllRepSpringTx() {
        val ex = assertFailsWith<BadSqlGrammarException> {
            service.saveAllRepSpringTx(
                listOf(
                    newAsdkContextDto("123"),
                    newAsdkContextDto("1".repeat(100))
                )
            ).blockLast()
        }
        log.error(ex) {}

        assertEquals(0, repository.count().block())
    }

    @Test
    @Order(3)
    fun saveAllRepReactorTx() {
        val ex = assertFailsWith<BadSqlGrammarException> {
            service.saveAllRepReactorTx(
                listOf(
                    newAsdkContextDto("123"),
                    newAsdkContextDto("1".repeat(100))
                )
            ).blockLast()
        }
        log.error(ex) {}

        assertEquals(0, repository.count().block())
    }

    @Test
    @Order(4)
    fun saveAllDaoNonTx() {
        val ex = assertFailsWith<DataException> {
            service.saveAllDaoNonTx(
                listOf(
                    newAsdkContext("123"),
                    newAsdkContext("1".repeat(100))
                )
            ).blockLast()
        }
        log.error("", ex)

        assertEquals(1, dao.count().block())
    }

    @Disabled("Spring tx not working with JOOQ")
    @Test
    @Order(5)
    fun saveAllDaoSpringTx() {
        val ex = assertFailsWith<DataException> {
            service.saveAllDaoSpringTx(
                listOf(
                    newAsdkContext("123"),
                    newAsdkContext("1".repeat(100))
                )
            ).blockLast()
        }
        log.error("", ex)

        assertEquals(0, dao.count().block())
    }

    @Disabled("Spring tx not working with JOOQ")
    @Test
    @Order(6)
    fun saveAllDaoReactorTx() {
        val ex = assertFailsWith<DataException> {
            service.saveAllDaoReactorTx(
                listOf(
                    newAsdkContext("123"),
                    newAsdkContext("1".repeat(100))
                )
            ).blockLast()
        }
        log.error("", ex)

        assertEquals(0, dao.count().block())
    }

    @Test
    @Order(7)
    fun saveAllDaoJooqTx() {
        val ex = assertFailsWith<DataException> {
            service.saveAllDaoJooqTx(
                listOf(
                    newAsdkContext("123"),
                    newAsdkContext("1".repeat(100))
                )
            ).blockLast()
        }
        log.error("", ex)

        assertEquals(0, dao.count().block())
    }

    @Test
    @Order(8)
    fun saveAllDaoJooqTxCor() {
        val ex = assertFailsWith<DataException> {
            runBlocking {
                service.saveAllDaoJooqTxCor(
                    listOf(
                        newAsdkContext("123"),
                        newAsdkContext("1".repeat(100))
                    )
                )
            }
        }
        log.error("", ex)

        assertEquals(0, dao.count().block())
    }

    private fun newAsdkContext(msg: String): SdkContext {
        return SdkContext(null, null, null, null, msg, null, null, null)
    }

    companion object {
        private fun newAsdkContextDto(msg: String): SdkContextDto {
            return SdkContextDto(msg = msg)
        }
    }
}

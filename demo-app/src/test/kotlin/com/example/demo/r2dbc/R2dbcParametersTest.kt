package com.example.demo.r2dbc

import com.example.demo.AbstractDatabaseTest
import io.r2dbc.spi.Parameters
import io.r2dbc.spi.R2dbcType
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class R2dbcParametersTest : AbstractDatabaseTest() {
    fun parameter(): List<String?> = listOf("abc", null)

    @ParameterizedTest
    @MethodSource
    fun parameter(config: String?) {
        val result = databaseClient.sql("select * from project where config = :config")
            .bind("config", Parameters.`in`(R2dbcType.VARCHAR, config))
            .fetch()
            .all()
            .collectList()
            .block()
        log.info("result = {}", result)
    }
}

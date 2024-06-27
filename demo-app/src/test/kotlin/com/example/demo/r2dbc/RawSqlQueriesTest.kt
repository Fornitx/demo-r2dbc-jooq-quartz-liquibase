package com.example.demo.r2dbc

import com.example.demo.AbstractDatabaseTest
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class RawSqlQueriesTest : AbstractDatabaseTest() {
    fun queries(): List<String> = listOf(
        "select current_schema()",
        "show search_path",
        "select version()",
        "select gen_random_uuid()",
        "select * from databasechangelog",
        "select unnest(array['Test_1', 'test_2', 'TEST_3']) as x order by x"
    )

    @ParameterizedTest
    @MethodSource("queries")
    fun databaseClient(sql: String) {
        val result = databaseClient.sql(sql).fetch().all().collectList().block()!!
        log.info(
            "result =\n{}",
            result.asSequence()
                .map { it.toString() }
                .joinToString(System.lineSeparator())
        )
    }
}

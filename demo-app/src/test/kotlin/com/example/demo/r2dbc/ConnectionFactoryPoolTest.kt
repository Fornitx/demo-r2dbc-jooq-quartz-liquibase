package com.example.demo.r2dbc

import com.example.demo.AbstractDatabaseTest
import io.r2dbc.pool.ConnectionPool
import io.r2dbc.spi.ConnectionFactory
import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class ConnectionFactoryPoolTest : AbstractDatabaseTest() {
    @Autowired
    private lateinit var connectionFactory: ConnectionFactory

    @Autowired
    private lateinit var connectionPool: ConnectionPool

    @Test
    fun test() {
        assertThat(connectionFactory).isInstanceOf(ConnectionPool::class.java)
        assertThat(connectionFactory).isEqualTo(connectionPool)
    }
}

package com.example.demo

import com.example.demo.utils.tc.containers.DemoPostgresContainer
import org.jooq.DSLContext
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate
import org.springframework.r2dbc.core.DatabaseClient
import org.testcontainers.lifecycle.Startables

abstract class AbstractDatabaseTest : AbstractTest() {
    // R2DBC
    @Autowired
    protected lateinit var databaseClient: DatabaseClient

    @Autowired
    protected lateinit var r2dbcEntityTemplate: R2dbcEntityTemplate

    // JOOQ
    @Autowired
    protected lateinit var dslContext: DSLContext

    companion object {
        @JvmStatic
        protected val postgresContainer = DemoPostgresContainer

        init {
            Startables.deepStart(postgresContainer).join()
        }
    }
}

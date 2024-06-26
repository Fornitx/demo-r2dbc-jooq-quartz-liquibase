package com.example.demo

import liquibase.Liquibase
import liquibase.database.DatabaseFactory
import liquibase.database.jvm.JdbcConnection
import liquibase.resource.ClassLoaderResourceAccessor
import java.sql.Connection

object LiquibaseInit {
    @JvmStatic
    fun init(connection: Connection) {
        val database = DatabaseFactory.getInstance()
            .findCorrectDatabaseImplementation(JdbcConnection(connection))

        Liquibase("init-for-jooq-codegen.sql", ClassLoaderResourceAccessor(), database)
            .update()

        database.defaultSchemaName = "context_schema_jooq"
        Liquibase("db/changelog/changelog-master.xml", ClassLoaderResourceAccessor(), database)
            .update()
    }
}

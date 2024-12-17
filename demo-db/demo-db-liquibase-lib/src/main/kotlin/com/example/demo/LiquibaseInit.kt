package com.example.demo

import liquibase.Liquibase
import liquibase.database.DatabaseFactory
import liquibase.database.jvm.JdbcConnection
import liquibase.resource.ClassLoaderResourceAccessor
import java.sql.Connection

fun init(connection: Connection) {
    val resourceAccessor = ClassLoaderResourceAccessor()
    val database = DatabaseFactory.getInstance()
        .findCorrectDatabaseImplementation(JdbcConnection(connection))

    Liquibase("liquibase/init-for-jooq-codegen.sql", resourceAccessor, database).update()

    database.defaultSchemaName = "demo_schema_codegen"
    Liquibase("liquibase/changelog/changelog-master.xml", resourceAccessor, database).update()
}

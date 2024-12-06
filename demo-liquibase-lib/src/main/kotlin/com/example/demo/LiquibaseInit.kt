package com.example.demo

import liquibase.Liquibase
import liquibase.database.DatabaseFactory
import liquibase.database.jvm.JdbcConnection
import liquibase.resource.DirectoryResourceAccessor
import java.io.File
import java.sql.Connection

fun init(connection: Connection) {
    val database = DatabaseFactory.getInstance()
        .findCorrectDatabaseImplementation(JdbcConnection(connection))

    Liquibase("init-for-jooq-codegen.sql", DirectoryResourceAccessor(File("../etc/db")), database)
        .update()

    database.defaultSchemaName = "demo_schema_codegen"
    Liquibase("changelog-master.xml", DirectoryResourceAccessor(File("../etc/db/changelog")), database)
        .update()
}

import liquibase.Liquibase
import liquibase.database.DatabaseFactory
import liquibase.database.jvm.JdbcConnection
import liquibase.resource.DirectoryResourceAccessor
import org.jooq.impl.DSL
import org.jooq.meta.jaxb.Logging
import org.testcontainers.containers.PostgreSQLContainer

plugins {
    `java-library`
    alias(libs.plugins.jooq.new)
}

dependencies {
    api(project(":demo-common"))
    api(libs.jooq.jackson.extensions)
}

buildscript {
    dependencies {
        classpath(platform(libs.spring.bom))
        classpath("org.testcontainers:postgresql")
        classpath("org.postgresql:postgresql")
        classpath("org.liquibase:liquibase-core")
    }
}

tasks.register("tcStart") {
    val rootProjectDir = rootProject.projectDir

    doLast {
        val db = PostgreSQLContainer("postgres:17-alpine")
        db.withReuse(true)
        db.start()

        // See https://www.jooq.org/doc/latest/manual/code-generation/codegen-system-properties/
        System.setProperty("jooq.codegen.jdbc.url", db.getJdbcUrl())
        System.setProperty("jooq.codegen.jdbc.username", db.username)
        System.setProperty("jooq.codegen.jdbc.password", db.password)
        System.setProperty("testcontainer.containerid", db.containerId)
        System.setProperty("testcontainer.imageName", db.dockerImageName)

        Class.forName("org.postgresql.Driver")

        // Alternatively, use Flyway, Liquibase, etc.
        DSL.using(db.getJdbcUrl(), "test", "test").use { ctx ->
            File("$rootProjectDir/etc/db/init-for-jooq-codegen.sql")
                .readText()
                .split(";")
                .forEach { if (it.isNotEmpty()) ctx.execute(it) }

            ctx.connection { conn ->
                val database = DatabaseFactory.getInstance()
                    .findCorrectDatabaseImplementation(JdbcConnection(conn))

                database.defaultSchemaName = "demo_schema_codegen"
                Liquibase(
                    "changelog-master.xml",
                    DirectoryResourceAccessor(File("$rootProjectDir/etc/db/changelog")),
                    database,
                ).update()
            }
        }
    }
}

jooq {
    configuration {
        logging = Logging.DEBUG
        generator {
            name = "org.jooq.codegen.KotlinGenerator"
            database {
                name = "org.jooq.meta.postgres.PostgresDatabase"
                inputSchema = "demo_schema_codegen"
                isOutputSchemaToDefault = true
                includes = ".*"
                excludes = "databasechangelog.*|qrtz_.*"

                forcedTypes {
                    forcedType {
                        userType = "com.example.demo.services.context.StatusEnum"
                        isEnumConverter = true
                        includeExpression = """
                                        sdk_context\.status
                                        | sdk_context_history\.status
                                    """.trimIndent()
                    }
                    forcedType {
                        userType = "com.example.demo.services.context.RuleDto"
                        isJsonConverter = true
                        includeExpression = """
                                        sdk_context\.rules
                                        | sdk_context_history\.rules
                                    """.trimIndent()
                    }
                }
            }
            generate {
                isImmutablePojos = true
                isPojos = true
                isPojosAsKotlinDataClasses = true
                isPojosEqualsAndHashCode = false
                isPojosToString = false
                isSerializablePojos = false
            }
            target {
                packageName = "com.example.demo.jooq.generated"
            }
        }
    }
}

tasks.register("tcStop") {
    doLast {
        val containerId = System.getProperty("testcontainer.containerid")
        val imageName = System.getProperty("testcontainer.imageName")

        println("Stopping testcontainer $containerId - $imageName")
        org.testcontainers.utility.ResourceReaper
            .instance()
            .stopAndRemoveContainer(containerId, imageName)
    }
}

tasks.named("compileKotlin") {
    dependsOn(tasks.named("jooqCodegen"))
}

tasks.named("jooqCodegen") {
    dependsOn("tcStart")
//    finalizedBy("tcStop")
}

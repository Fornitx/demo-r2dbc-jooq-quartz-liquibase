plugins {
    `java-library`
    alias(libs.plugins.jooq.old)
}

dependencyManagement {
    imports {
        mavenBom(libs.spring.bom.get().toString())
    }
}

val jooqVersion = dependencyManagement.importedProperties["jooq.version"]

dependencies {
    api("org.jooq:jooq-jackson-extensions:$jooqVersion")
    api(project(":demo-common"))

    jooqGenerator("org.postgresql:postgresql")
    jooqGenerator("org.testcontainers:postgresql")
    jooqGenerator("org.slf4j:slf4j-simple")
    jooqGenerator(project(":demo-db:demo-db-liquibase"))
    jooqGenerator(project(":demo-db:demo-db-liquibase-lib"))
}

jooq {
    version.set(jooqVersion)

    configurations {
        create("main") {
            generateSchemaSourceOnCompilation.set(true)

            jooqConfiguration.apply {
                jdbc.apply {
                    driver = "org.testcontainers.jdbc.ContainerDatabaseDriver"
                    url = "jdbc:tc:postgresql:17-alpine:///test" +
                            "?TC_REUSABLE=true" +
                            "&TC_INITFUNCTION=com.example.demo.LiquibaseInitKt::init"
                    user = "test"
                    password = "test"
                }

                generator.apply {
                    name = "org.jooq.codegen.KotlinGenerator"
                    database.apply {
                        name = "org.jooq.meta.postgres.PostgresDatabase"
                        inputSchema = "demo_schema_codegen"
                        isOutputSchemaToDefault = true
                        includes = ".*"
                        excludes = "databasechangelog.*|qrtz_.*"

                        withForcedTypes(
                            org.jooq.meta.jaxb.ForcedType().apply {
                                userType = "com.example.demo.services.context.StatusEnum"
                                isEnumConverter = true
                                includeExpression = """
                                        sdk_context\.status
                                        | sdk_context_history\.status
                                    """.trimIndent()
                            },
                            org.jooq.meta.jaxb.ForcedType().apply {
                                userType = "com.example.demo.services.context.RuleDto"
                                isJsonConverter = true
                                includeExpression = """
                                        sdk_context\.rules
                                        | sdk_context_history\.rules
                                    """.trimIndent()
                            },
                        )
                    }
                    generate.apply {
                        isImmutablePojos = true
                        isPojos = true
                        isPojosAsKotlinDataClasses = true
                        isPojosEqualsAndHashCode = false
                        isPojosToString = false
                        isSerializablePojos = false
                    }
                    target.apply {
                        packageName = "com.example.demo.jooq.generated"
                    }
                    strategy.name = "org.jooq.codegen.DefaultGeneratorStrategy"
                }
            }
        }
    }
}

tasks.withType<nu.studer.gradle.jooq.JooqGenerate> {
    allInputsDeclared.set(true)
    javaExecSpec = Action {
        environment("TESTCONTAINERS_CHECKS_DISABLE", true)
        systemProperty("org.jooq.no-logo", true)
        systemProperty("org.jooq.no-tips", true)
    }
}

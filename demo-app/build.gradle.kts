plugins {
    id("org.springframework.boot")
    id("io.spring.dependency-management")
    kotlin("jvm")
    kotlin("plugin.spring")

    id("nu.studer.jooq") version "9.0"
//    id("org.jooq.jooq-codegen-gradle") version "3.19.10"
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

val jooqVersion = dependencyManagement.importedProperties["jooq.version"]
ext["kotlin-coroutines.version"] = System.getProperty("kotlin_coroutines_version")

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-data-r2dbc")
    implementation("org.springframework.boot:spring-boot-starter-jooq")
    implementation("org.springframework.boot:spring-boot-starter-quartz")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-webflux")

    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("io.projectreactor.kotlin:reactor-kotlin-extensions")

    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")

    implementation("io.github.oshai:kotlin-logging-jvm:" + System.getProperty("kotlin_logging_version"))

    implementation("org.jooq:jooq-jackson-extensions:$jooqVersion")
    implementation("org.jooq:jooq-kotlin-coroutines:$jooqVersion")

    runtimeOnly("org.postgresql:r2dbc-postgresql")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.boot:spring-boot-testcontainers")

    testImplementation("io.projectreactor:reactor-test")

    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test")

    testImplementation("org.testcontainers:junit-jupiter")
    testImplementation("org.testcontainers:postgresql")
//	testImplementation("org.testcontainers:r2dbc")

    testImplementation(project(":demo-db"))

    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    jooqGenerator("org.testcontainers:postgresql")
    jooqGenerator(project(":demo-db"))
}

kotlin {
    compilerOptions {
        freeCompilerArgs.addAll("-Xjsr305=strict")
    }
}

tasks.withType<Test> {
    environment("TESTCONTAINERS_CHECKS_DISABLE", true)
    systemProperty("org.jooq.no-logo", true)
    systemProperty("org.jooq.no-tips", true)
    useJUnitPlatform()
}

tasks.jar {
    enabled = false
}

jooq {
    version.set(jooqVersion)

    configurations {
        create("main") {
//            generateSchemaSourceOnCompilation.set(false )

            jooqConfiguration.apply {
                jdbc.apply {
                    driver = "org.testcontainers.jdbc.ContainerDatabaseDriver"
                    url = "jdbc:tc:postgresql:13-alpine:///test?TC_INITFUNCTION=com.example.demo.LiquibaseInit::init"
                    user = "test"
                    password = "test"
                }

                generator.apply {
                    name = "org.jooq.codegen.KotlinGenerator"
                    database.apply {
                        name = "org.jooq.meta.postgres.PostgresDatabase"
                        inputSchema = "context_schema_jooq"
                        isOutputSchemaToDefault = true
                        includes = ".*"
                        excludes = """
                            databasechangelog.*
                            | qrtz.*
                        """.trimIndent()

                        forcedTypes.addAll(
                            listOf(
                                org.jooq.meta.jaxb.ForcedType().apply {
                                    userType = "com.example.demo.services.css.StatusEnum"
                                    isEnumConverter = true
                                    includeExpression = """
                                        asdk_context\.status
                                        | asdk_context_history\.status
                                    """.trimIndent()
                                },
                                org.jooq.meta.jaxb.ForcedType().apply {
                                    userType = "com.example.demo.services.css.RuleDto"
                                    isJsonConverter = true
                                    includeExpression = """
                                        asdk_context\.rules
                                        | asdk_context_history\.rules
                                    """.trimIndent()
                                },
                            )
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
                        packageName = "com.example.demo.jooq"
                    }
                    strategy.name = "org.jooq.codegen.DefaultGeneratorStrategy"
                }
            }
        }
    }
}

tasks.named<nu.studer.gradle.jooq.JooqGenerate>("generateJooq") {
    javaExecSpec = Action {
        environment("TESTCONTAINERS_CHECKS_DISABLE", true)
        systemProperty("org.jooq.no-logo", true)
        systemProperty("org.jooq.no-tips", true)
    }
}

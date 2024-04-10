import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.springframework.boot")
    id("io.spring.dependency-management")
    kotlin("jvm")
    kotlin("plugin.spring")

    id("nu.studer.jooq") version "9.0"
}

java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}

val jooqVersion = dependencyManagement.importedProperties["jooq.version"]

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

//	implementation("org.liquibase:liquibase-core")
//	implementation("org.springframework:spring-jdbc")

    implementation("io.github.oshai:kotlin-logging-jvm:" + System.getProperty("kotlin_logging_version"))

    implementation("org.jooq:jooq-jackson-extensions:$jooqVersion")
    implementation("org.jooq:jooq-kotlin-coroutines:$jooqVersion")

	runtimeOnly("org.postgresql:postgresql")
	runtimeOnly("org.postgresql:r2dbc-postgresql")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("org.springframework.boot:spring-boot-testcontainers")

    testImplementation("io.projectreactor:reactor-test")
    testImplementation("org.jetbrains.kotlin:kotlin-test")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test")

    testImplementation("org.testcontainers:junit-jupiter")
	testImplementation("org.testcontainers:postgresql")
	testImplementation("org.testcontainers:r2dbc")

    testImplementation(project(":demo-db"))

    jooqGenerator("org.slf4j:slf4j-simple")
    jooqGenerator("org.postgresql:postgresql")
    jooqGenerator("org.testcontainers:postgresql")
    jooqGenerator(project(":demo-db"))
}


tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs += listOf("-Xjsr305=strict", "-Xemit-jvm-type-annotations")
        jvmTarget = "21"
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.jar {
    enabled = false
}

jooq {
    version.set(jooqVersion)

    configurations {
        create("main") {
            generateSchemaSourceOnCompilation.set(true)

            jooqConfiguration.apply {
//                logging = org.jooq.meta.jaxb.Logging.WARN

                jdbc.apply {
                    driver = "org.testcontainers.jdbc.ContainerDatabaseDriver"
                    url = "jdbc:tc:postgresql:13-alpine:///test?TC_INITFUNCTION=com.example.demo.LiquibaseInit::init"
                    user = "test"
                    password = "test"
                }

                generator.apply {
                    name = "org.jooq.codegen.KotlinGenerator"
//                    name = "org.jooq.codegen.DefaultGenerator"
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
                                    userType = "com.example.demo.data.css.StatusEnum"
                                    isEnumConverter = true
                                    includeExpression = """
                                        asdk_context\.status
                                        | asdk_context_history\.status
                                    """.trimIndent()
                                },
                                org.jooq.meta.jaxb.ForcedType().apply {
                                    userType = "com.example.demo.data.css.RuleDto"
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

//                        isDeprecated = false
//                        isRecords = true
//                        isImmutablePojos = true
//                        isPojosAsJavaRecordClasses = true
//                        isPojosEqualsAndHashCode = false
//                        isPojosToString = false
//                        isFluentSetters = true
                        isPojos = true
                        isPojosAsKotlinDataClasses = true
//                        isDaos = true
//                        isSpringAnnotations = true
//                        isSpringDao = true
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

tasks.named<nu.studer.gradle.jooq.JooqGenerate>("generateJooq") {
    allInputsDeclared.set(true)
    javaExecSpec = Action {
        environment("TESTCONTAINERS_CHECKS_DISABLE", true)
        systemProperty("org.jooq.no-logo", true)
        systemProperty("org.jooq.no-tips", true)
    }
}

plugins {
    alias(libs.plugins.kotlin.spring)
    alias(libs.plugins.spring.boot)
    alias(libs.plugins.spring.dm)
}

val jooqVersion = dependencyManagement.importedProperties["jooq.version"]
require(jooqVersion == libs.versions.jooq.get()) { "jooqVersion not synchronized with Spring Boot" }

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

    implementation(libs.kotlin.logging)

    implementation("org.jooq:jooq-jackson-extensions:$jooqVersion")
    implementation("org.jooq:jooq-kotlin-coroutines:$jooqVersion")

    implementation(project(":demo-db:demo-db-jooq-old"))

    runtimeOnly("org.postgresql:postgresql")
    runtimeOnly("org.postgresql:r2dbc-postgresql")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.boot:spring-boot-testcontainers")

    testImplementation("io.projectreactor:reactor-test")

    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test")

    testImplementation("org.testcontainers:junit-jupiter")
    testImplementation("org.testcontainers:postgresql")
//	testImplementation("org.testcontainers:r2dbc")

    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    testRuntimeOnly("org.liquibase:liquibase-core")
    testRuntimeOnly(project(":demo-db:demo-db-liquibase"))
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

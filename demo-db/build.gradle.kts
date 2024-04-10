plugins {
    kotlin("jvm")
}

dependencies {
    implementation(platform("org.springframework.boot:spring-boot-dependencies:${System.getProperty("spring_version")}"))

    implementation("org.liquibase:liquibase-core")
}

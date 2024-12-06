plugins {
    `java-library`
}

dependencies {
    implementation(platform(libs.spring.bom))
    implementation("org.liquibase:liquibase-core")
}

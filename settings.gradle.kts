pluginManagement {
    plugins {
        id("org.springframework.boot") version System.getProperty("spring_version")
        id("io.spring.dependency-management") version System.getProperty("spring_dm_version")
        kotlin("jvm") version System.getProperty("kotlin_version")
        kotlin("plugin.spring") version System.getProperty("kotlin_version")
    }
}

dependencyResolutionManagement {
    repositories {
        mavenCentral()
    }
}

rootProject.name = "demo-r2dbc-jooq-quartz-liquibase"

include("demo-app", "demo-app2", "demo-db")

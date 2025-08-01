dependencyResolutionManagement {
    repositoriesMode = RepositoriesMode.FAIL_ON_PROJECT_REPOS
    repositories {
        mavenCentral()
    }
    versionCatalogs {
        create("libs") {
            version("kotlin-lang", providers.gradleProperty("kotlin1-lang.version").get())
            version("kotlin-logging", providers.gradleProperty("kotlin1-logging.version").get())
            version("spring-boot", providers.gradleProperty("spring-boot.version").get())
            version("spring-dm", providers.gradleProperty("spring-dm.version").get())
        }
    }
}

rootProject.name = "demo-r2dbc-jooq-quartz-liquibase"

include("demo-common")
include("demo-app")
include(
    "demo-db:demo-db-jooq-old",
//    "demo-db:demo-db-jooq-new",
    "demo-db:demo-db-liquibase",
    "demo-db:demo-db-liquibase-lib",
)

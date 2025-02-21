plugins {
    alias(libs.plugins.kotlin.jvm)
}

allprojects {
    group = "org.example"
    version = "1.0"

//    dependencies {
//        constraints {
//            implementation("org.jetbrains.kotlin:kotlin-reflect:" + rootProject.libs.versions.kotlin.lang.get())
//        }
//    }

    tasks.register<DependencyReportTask>("allDeps") {}
}

subprojects {
    apply(plugin = rootProject.libs.plugins.kotlin.jvm.get().pluginId)

    java {
        toolchain {
            languageVersion = JavaLanguageVersion.of(21)
        }
    }

    kotlin {
        compilerOptions {
            freeCompilerArgs.addAll("-Xjsr305=strict")
        }
    }

    configurations.all {
        resolutionStrategy.dependencySubstitution {
            substitute(module("junit:junit"))
                .using(module("io.quarkus:quarkus-junit4-mock:3.18.0"))
                .because(
                    "We don't want JUnit 4; but is an unneeded transitive of testcontainers. " +
                        "See https://github.com/testcontainers/testcontainers-java/issues/970"
                )
        }
    }
}

tasks.build {
    enabled = false
}

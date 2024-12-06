plugins {
    alias(libs.plugins.kotlin.jvm)
}

allprojects {
    apply(plugin = rootProject.libs.plugins.kotlin.jvm.get().pluginId)

    group = "org.example"
    version = "1.0"

    java {
        toolchain {
            languageVersion = JavaLanguageVersion.of(21)
        }
    }

    dependencies {
        constraints {
            implementation("org.jetbrains.kotlin:kotlin-reflect:" + rootProject.libs.versions.kotlin.lang.get())
        }
    }

    kotlin {
        compilerOptions {
            freeCompilerArgs.addAll("-Xjsr305=strict")
        }
    }

    tasks.register<DependencyReportTask>("allDeps") {}
}

tasks.build {
    enabled = false
}

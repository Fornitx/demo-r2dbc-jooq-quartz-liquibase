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
}

tasks.build {
    enabled = false
}

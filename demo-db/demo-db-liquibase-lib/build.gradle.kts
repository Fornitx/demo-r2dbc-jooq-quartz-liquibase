plugins {
}

dependencyManagement {
    imports {
        mavenBom(libs.spring.bom.get().toString())
    }
}

dependencies {
//    implementation(platform(libs.spring.bom))
    implementation("org.liquibase:liquibase-core")
}

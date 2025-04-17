package com.example.demo.utils.tc.containers

import com.example.demo.utils.tc.TestContainersHelper
import com.github.dockerjava.api.model.ExposedPort
import com.github.dockerjava.api.model.PortBinding
import com.github.dockerjava.api.model.Ports
import io.github.oshai.kotlinlogging.KotlinLogging
import org.testcontainers.containers.PostgreSQLContainer

private val log = KotlinLogging.logger {}

object DemoPostgresContainer : PostgreSQLContainer<DemoPostgresContainer>(TestContainersHelper.POSTGRES_IMAGE) {
    init {
        withReuse(true)
        withInitScript("init.sql")
        withCreateContainerCmdModifier { createContainerCmd ->
            createContainerCmd.hostConfig!!.withPortBindings(
                PortBinding(
                    Ports.Binding.bindPort(POSTGRESQL_PORT),
                    ExposedPort(POSTGRESQL_PORT)
                )
            )
        }
    }

    override fun start() {
        super.start()

        System.setProperty("TC_PG_PORT", getMappedPort(POSTGRESQL_PORT).toString())
        System.setProperty("TC_PG_DATABASE", databaseName)
        System.setProperty("TC_PG_USERNAME", username)
        System.setProperty("TC_PG_PASSWORD", password)
//        System.setProperty("TC_PG_SCHEMA", "demo_schema")

        log.info { "\nPostgres container started: $jdbcUrl" }
    }
}

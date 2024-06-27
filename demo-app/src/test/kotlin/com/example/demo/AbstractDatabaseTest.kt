package com.example.demo

import com.github.dockerjava.api.model.ExposedPort
import com.github.dockerjava.api.model.PortBinding
import com.github.dockerjava.api.model.Ports
import io.github.oshai.kotlinlogging.KotlinLogging
import org.jooq.DSLContext
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate
import org.springframework.r2dbc.core.DatabaseClient
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.utility.DockerImageName
import org.testcontainers.utility.TestcontainersConfiguration

abstract class AbstractDatabaseTest : AbstractTest() {
    // R2DBC
    @Autowired
    protected lateinit var databaseClient: DatabaseClient

    @Autowired
    protected lateinit var r2dbcEntityTemplate: R2dbcEntityTemplate

    // JOOQ
    @Autowired
    protected lateinit var dslContext: DSLContext

    companion object {
        private val log = KotlinLogging.logger {}

        protected val postgresContainer: PostgreSQLContainer<*> = PostgreSQLContainer(
            DockerImageName.parse(
                TestcontainersConfiguration.getInstance().getEnvVarOrProperty("postgres.container.image", null)
            )
                .asCompatibleSubstituteFor("postgres")
        )
            .withDatabaseName("postgres")
            .withUsername("postgres")
            .withPassword("postgres")
            .withInitScript("init.sql")
            .withCreateContainerCmdModifier { createContainerCmd ->
                createContainerCmd.hostConfig!!.withPortBindings(
                    PortBinding(
                        Ports.Binding.bindPort(PostgreSQLContainer.POSTGRESQL_PORT),
                        ExposedPort(PostgreSQLContainer.POSTGRESQL_PORT)
                    )
                )
            }
            .withReuse(true)

        init {
            postgresContainer.start()

            System.setProperty(
                "POSTGRES_PORT",
                postgresContainer.getMappedPort(PostgreSQLContainer.POSTGRESQL_PORT).toString()
            )
            System.setProperty("POSTGRES_DATABASE", postgresContainer.databaseName)
            System.setProperty("POSTGRES_SCHEMA", "context_schema")
            System.setProperty("POSTGRES_USERNAME", postgresContainer.username)
            System.setProperty("POSTGRES_PASSWORD", postgresContainer.password)

            log.info("\nPostgres container started: {}", postgresContainer.jdbcUrl)
        }
    }
}

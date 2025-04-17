package com.example.demo.utils.tc

import org.testcontainers.utility.TestcontainersConfiguration

object TestContainersHelper {
    private val tcConfig = TestcontainersConfiguration.getInstance()

    val POSTGRES_IMAGE = tcConfig.getEnvVarOrProperty("postgres.container.image", null)
}

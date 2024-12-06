package com.example.demo.quartz

import io.github.oshai.kotlinlogging.KotlinLogging

private val log = KotlinLogging.logger {}

class DemoJobService {
    fun callJob() {
        log.info { "Job called." }
    }
}

package com.example.demo.services.project

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper

data class RawJson(val data: String) {
    init {
        OBJECT_MAPPER.readTree(data)
    }

    companion object {
        private val OBJECT_MAPPER = jacksonObjectMapper()
    }
}

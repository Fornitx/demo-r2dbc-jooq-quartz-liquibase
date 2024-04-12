package com.example.demo.r2dbc

import com.example.demo.services.appdir.RawJson
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.convert.converter.Converter
import org.springframework.data.r2dbc.convert.R2dbcCustomConversions
import org.springframework.data.r2dbc.dialect.PostgresDialect

@Configuration
class R2dbcConfig {
    @Bean
    fun r2dbcCustomConversions(objectMapper: ObjectMapper): R2dbcCustomConversions {
        val converters = listOf(
            StringToRawJsonConverter(),
            RawJsonToStringConverter(),
        )
        return R2dbcCustomConversions.of(PostgresDialect.INSTANCE, converters)
    }

    private class StringToRawJsonConverter : Converter<String, RawJson> {
        override fun convert(source: String): RawJson {
            return RawJson(source)
        }
    }

    private class RawJsonToStringConverter : Converter<RawJson, String> {
        override fun convert(source: RawJson): String {
            return source.data
        }
    }
}

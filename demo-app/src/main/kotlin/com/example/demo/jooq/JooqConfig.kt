package com.example.demo.jooq

import com.example.demo.services.back.BackDao
import com.example.demo.services.context.SdkContextDaoImpl
import io.r2dbc.spi.ConnectionFactory
import org.jooq.DSLContext
import org.jooq.impl.DSL
import org.springframework.boot.autoconfigure.jooq.JooqProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
@EnableConfigurationProperties(JooqProperties::class)
class JooqConfig {
    @Bean
    fun dslContext(properties: JooqProperties, connectionFactory: ConnectionFactory): DSLContext {
//        val settings = Settings()
//        settings.setFetchSize(5)
        return DSL.using(connectionFactory, properties.sqlDialect /*, settings*/)
    }

    @Bean
    fun asdkContextDao(dslContext: DSLContext) = SdkContextDaoImpl(dslContext)

    @Bean
    fun iabDao(dslContext: DSLContext) = BackDao(dslContext)
}

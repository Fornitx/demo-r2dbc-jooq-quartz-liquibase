package com.example.demo.services.context

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.time.OffsetDateTime
import java.util.*

@Table("sdk_context")
data class SdkContextDto(
    @Id
    val id: UUID? = null,
    val version: Int? = null,
    val createdAt: OffsetDateTime? = null,
    val updatedAt: OffsetDateTime? = null,
    val msg: String? = null,
    val status: StatusEnum? = null,
    val rules: RuleDto? = null,
    //    private RawJson config;
//    private val config: Json,
)

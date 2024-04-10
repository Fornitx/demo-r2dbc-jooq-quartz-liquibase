package com.example.demo.data.css

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.time.OffsetDateTime
import java.util.*

@Table("asdk_context")
data class AsdkContextDto(
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

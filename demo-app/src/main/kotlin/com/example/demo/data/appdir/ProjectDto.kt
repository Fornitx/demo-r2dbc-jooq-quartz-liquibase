package com.example.demo.data.appdir

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.time.OffsetDateTime
import java.util.*

@Table("project")
data class ProjectDto(
    @Id
    val id: UUID? = null,
    val version: Int? = null,
    val createdAt: OffsetDateTime? = null,
    val updatedAt: OffsetDateTime? = null,
    val config: RawJson? = null,
)

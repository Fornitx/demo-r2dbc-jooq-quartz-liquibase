package com.example.demo.data.appdir

import org.springframework.data.r2dbc.repository.R2dbcRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface ProjectRepository : R2dbcRepository<ProjectDto, UUID>

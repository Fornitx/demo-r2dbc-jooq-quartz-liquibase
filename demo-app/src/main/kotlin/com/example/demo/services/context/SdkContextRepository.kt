package com.example.demo.services.context

import org.springframework.data.r2dbc.repository.R2dbcRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface SdkContextRepository : R2dbcRepository<SdkContextDto, UUID>

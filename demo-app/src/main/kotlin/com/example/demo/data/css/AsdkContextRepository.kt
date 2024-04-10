package com.example.demo.data.css

import org.springframework.data.r2dbc.repository.R2dbcRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface AsdkContextRepository : R2dbcRepository<AsdkContextDto, UUID>

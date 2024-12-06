package com.example.demo.services.project

import com.example.demo.AbstractDatabaseTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import reactor.kotlin.test.test

@SpringBootTest
internal class ProjectRepositoryTest : AbstractDatabaseTest() {
    @Autowired
    private lateinit var repository: ProjectRepository

    @AfterEach
    fun cleanUp() {
        repository.deleteAll().block()
    }

    @Test
    fun test() {
        val projectDto = ProjectDto(
            config = RawJson("{\"a\":\"b\"}")
        )

        val saved = repository.save(projectDto).block()!!
        log.info("saved = {}", saved)

        assertThat(saved.id).isNotNull()
        assertThat(saved.version).isNull()
        assertThat(saved.createdAt).isNull()
        assertThat(saved.updatedAt).isNull()

        assertThat(saved).usingRecursiveComparison()
            .ignoringExpectedNullFields()
            .isEqualTo(projectDto)

        repository.count().test().expectNext(1).verifyComplete()
        repository.findById(saved.id!!).test().expectNextCount(1).verifyComplete()
    }
}

package com.example.demo.data.css;

import com.example.demo.AbstractDatabaseTest;
import io.r2dbc.postgresql.codec.Json;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Slf4j
class AsdkContextRepositoryTest extends AbstractDatabaseTest {
    @Autowired
    private AsdkContextRepository repository;

    @AfterEach
    void cleanUp() {
        repository.deleteAll().block();
    }

    @Test
    void test() {
        var projectDto = AsdkContextDto.builder()
            .msg("Message")
            .status(StatusEnum.THREE)
            .rules(new RuleDto(true, Set.of()))
//            .config(new RawJson("""
//                {"a":"b"}
//                """))
            .config(Json.of("""
                {"a":"b"}
                """))
            .build();

        var saved = repository.save(projectDto).block();
        log.info("saved = {}", saved);
        assertThat(saved).usingRecursiveComparison()
            .ignoringExpectedNullFields()
            .isEqualTo(projectDto);
    }
}

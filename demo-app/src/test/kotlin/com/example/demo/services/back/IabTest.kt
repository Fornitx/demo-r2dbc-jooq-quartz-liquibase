package com.example.demo.services.back

import com.example.demo.AbstractDatabaseTest
import kotlinx.coroutines.async
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class IabTest : AbstractDatabaseTest() {
    @Autowired
    private lateinit var backDao: BackDao

    @Test
    fun test() = runTest {
        val services = async { backDao.findServices() }
        val methods = async { backDao.findMethods() }

        services.await().also { println("services: ${it.size}") }.forEach { println(it) }
        methods.await().also { println("methods: ${it.size}") }.forEach { println(it) }
    }
}

package com.mitchmele.algorithmcloudprocessor

import com.mitchmele.algorithmcloudprocessor.utils.IntegrationTest
import org.junit.experimental.categories.Category
import org.junit.runner.RunWith
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Configuration
import org.springframework.data.mongodb.config.AbstractMongoConfiguration
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner


@RunWith(SpringJUnit4ClassRunner::class)
@SpringBootTest(classes = [AlgorithmRepositoryIntegrationTest.TestConfig::class])
@Category(IntegrationTest::class)
class AlgorithmRepositoryIntegrationTest {







    @Configuration
    @EnableMongoRepositories(basePackages = ["com.mitchmele.*"])
    class TestConfig : AbstractMongoConfiguration() {
        override fun mongoClient(): com.mongodb.MongoClient {
            return com.mongodb.MongoClient("0.0.0.0", 27017)
        }

        override fun getDatabaseName(): String {
            return "testAlgorithmDomainModels"
        }
    }
}
package com.mitchmele.algorithmcloudprocessor.services

import com.mitchmele.algorithmcloudprocessor.common.Success
import com.mitchmele.algorithmcloudprocessor.store.AlgorithmDomainModel
import com.mitchmele.algorithmcloudprocessor.store.AlgorithmDomainModels
import com.mitchmele.algorithmcloudprocessor.store.Tag
import com.mitchmele.algorithmcloudprocessor.utils.UnitTest
import com.nhaarman.mockito_kotlin.*
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import org.junit.experimental.categories.Category

@Category(UnitTest::class)
class MongoDupeValidatorServiceTest {

    val algorithmDomainModel = AlgorithmDomainModel(
        name = "countDupes",
        codeSnippet = """
            fun countDupes(arr: Array<Int>): Int = arr.size - arr.distinct()
        """.trimIndent(),
        category = com.mitchmele.algorithmcloudprocessor.store.Category(
            categoryDescription = "EASY",
            difficultyLevel = 2,
            tags = listOf(Tag("Collections"))
        ),
        isSolved = false
    )

    val algorithmDomainModel2 = AlgorithmDomainModel(
        name = "someAlgo",
        codeSnippet = """
            fun someAlgo(arr: Array<Int>): Int = -1
        """.trimIndent(),
        category = com.mitchmele.algorithmcloudprocessor.store.Category(
            categoryDescription = "MEDIUM",
            difficultyLevel = 2,
            tags = listOf(Tag("Random"))
        )
    )

    val mockMongoClient: MongoClient = mock {
        on { loadAllAlgorithms() } doReturn Success(AlgorithmDomainModels(listOf(
            algorithmDomainModel,
            algorithmDomainModel2
        )))
    }

    val subject = MongoValidationService(mockMongoClient)


    @Test
    fun `validation - success - should call mongo to get all algorithms`() {
        whenever(mockMongoClient.loadAllAlgorithms()) doReturn Success(
            AlgorithmDomainModels(listOf(
                algorithmDomainModel,
                algorithmDomainModel
            )))

        subject.validate(AlgorithmDomainModel(name = "algoOne"))

        verify(mockMongoClient).loadAllAlgorithms()
    }

    @Test
    fun `validation - success - should return true if incoming algo name is not present in db`() {

        whenever(mockMongoClient.loadAllAlgorithms()) doReturn Success(
            AlgorithmDomainModels(listOf(
                algorithmDomainModel,
                algorithmDomainModel
            )))

        val actual = subject.validate(AlgorithmDomainModel(name = "algoOne"))

        assertThat(actual).isTrue()
    }

    @Test
    fun `validation - failure - should return false if incoming algo name is in db, case insensitive`() {

        whenever(mockMongoClient.loadAllAlgorithms()) doReturn Success(
            AlgorithmDomainModels(listOf(
                algorithmDomainModel,
                algorithmDomainModel
            )))

        val actual = subject.validate(AlgorithmDomainModel(name = "countdupes"))

        assertThat(actual).isFalse()
    }
}

package com.mitchmele.algorithmcloudprocessor.services

import assertk.all
import assertk.assertThat
import assertk.assertions.isEqualTo
import com.mitchmele.algorithmcloudprocessor.mongodb.AlgorithmMongoRepository
import com.mitchmele.algorithmcloudprocessor.result.*
import com.mitchmele.algorithmcloudprocessor.store.AlgorithmDomainModel
import com.mitchmele.algorithmcloudprocessor.store.Tag
import com.mitchmele.algorithmcloudprocessor.utils.UnitTest
import com.nhaarman.mockito_kotlin.*
import org.junit.Test
import org.junit.experimental.categories.Category
import java.lang.RuntimeException

@Category(UnitTest::class)
class MongoClientTest {

    val mockRepo: AlgorithmMongoRepository = mock()

    val subject = MongoClient(mockRepo)

    val inputAlgorithmDomainModel = AlgorithmDomainModel(
        name = "countDupes",
        codeSnippet = """
            fun countDupes(arr: Array<Int>): Int = arr.size - arr.distinct()
        """.trimIndent(),
        category = com.mitchmele.algorithmcloudprocessor.store.Category(
            categoryDescription = "EASY",
            difficultyLevel = 2,
            tags = listOf(Tag("Collections"))
        )
    )


    @Test
    fun `saveAlgorithm - should call the repository to retrieve an algorithm`() {

        subject.saveAlgorithm(inputAlgorithmDomainModel)

        verify(mockRepo).save(any<AlgorithmDomainModel>())
    }

    @Test
    fun `saveAlgorithm - should return a Success after successfully inserting an algorithm into Mongo`() {

        whenever(mockRepo.save(any<AlgorithmDomainModel>())) doReturn inputAlgorithmDomainModel

        subject.saveAlgorithm(inputAlgorithmDomainModel).succeedsAnd { result ->
            assertThat(result).all {
                isEqualTo(Unit)
            }
        }
    }

    @Test
    fun `saveAlgorithm - should return a Failure with ServiceErrors if the repo throws an exception when saving`() {

        val expected = serviceErrorOf(ServiceError(
            service = ServiceName.MONGO,
            errorMessage = "some exception",
            errorType = ErrorType.INPUT_VALIDATION
        ))

        whenever(mockRepo.save(any<AlgorithmDomainModel>())) doThrow RuntimeException("some exception")

        subject.saveAlgorithm(inputAlgorithmDomainModel).let { result ->
            assertThat(result).isEqualTo(Failure(expected))
        }
    }
}
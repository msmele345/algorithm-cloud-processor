package com.mitchmele.algorithmcloudprocessor.services

import assertk.all
import assertk.assertThat
import assertk.assertions.isEqualTo
import com.mitchmele.algorithmcloudprocessor.mongodb.AlgorithmMongoRepository
import com.mitchmele.algorithmcloudprocessor.common.*
import com.mitchmele.algorithmcloudprocessor.store.AlgorithmDomainModel
import com.mitchmele.algorithmcloudprocessor.store.AlgorithmDomainModels
import com.mitchmele.algorithmcloudprocessor.store.Category
import com.mitchmele.algorithmcloudprocessor.store.Tag
import com.mitchmele.algorithmcloudprocessor.utils.UnitTest
import com.nhaarman.mockito_kotlin.*
import org.junit.Test
import org.junit.experimental.categories.Category as TestCategory
import java.lang.RuntimeException

@TestCategory(UnitTest::class)
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

    val mockAlgorithmDomainModel = AlgorithmDomainModel(
        name = "countDupes",
        codeSnippet = """
            fun countDupes(arr: Array<Int>): Int = arr.size - arr.distinct()
        """.trimIndent(),
        category = Category(
            categoryDescription = "EASY",
            difficultyLevel = 2,
            tags = listOf(Tag("Collections"))
        ),
        isSolved = false
    )

    val mockAlgorithmDomainModel2 = AlgorithmDomainModel(
        name = "some algo",
        codeSnippet = """
            fun someAlgo(arr: Array<Int>): Int = -1
        """.trimIndent(),
        category = Category(
            categoryDescription = "EASY",
            difficultyLevel = 2,
            tags = listOf(Tag("Random"))
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


    @Test
    fun `deleteAlgorithmByName - should invoke the repository method delete by Id when called`() {

        val incomingAlgorithmToDelete = AlgorithmDomainModel(
            name = "badAlgo"
        )

        subject.deleteAlgorithmByName("badAlgo").let {
            verify(mockRepo).deleteById("badAlgo")
        }
    }

    @Test
    fun `deleteAlgorithmByName - should return a failure with ServiceErrors if delete throws an exception`() {
        val expected = serviceErrorOf(ServiceError(
            service = ServiceName.MONGO,
            errorMessage = "some exception with deleting",
            errorType = ErrorType.UNKNOWN_ERROR
        ))

        val incomingAlgorithmToDelete = AlgorithmDomainModel(
            name = "badAlgo"
        )

        whenever(mockRepo.deleteById(any())) doThrow RuntimeException("some exception with deleting")

        subject.deleteAlgorithmByName("badAlgo").failsAnd { result ->
            assertThat(result).isEqualTo(expected)
        }
    }

    @Test
    fun `loadAllAlgorithms - success - should call the repo method findAll`() {

        whenever(mockRepo.findAll()) doReturn listOf(mockAlgorithmDomainModel, mockAlgorithmDomainModel2)

        val actual = subject.loadAllAlgorithms()
        verify(mockRepo).findAll()
    }


    @Test
    fun `loadAllAlgorithms - success - should return a list of AlgorithmDomainModels from mongo db`() {

        whenever(mockRepo.findAll()) doReturn listOf(mockAlgorithmDomainModel, mockAlgorithmDomainModel2)

        subject.loadAllAlgorithms().let { actual ->
            assertThat(actual).isEqualTo(Success(AlgorithmDomainModels(listOf(
                mockAlgorithmDomainModel,
                mockAlgorithmDomainModel2
            ))))
        }
    }

    @Test
    fun `loadAllAlgorithms - failure - should return a ServiceError if findAll fails`() {

        val expected = serviceErrorOf(ServiceError(
            service = ServiceName.MONGO,
            errorMessage = "some error",
            errorType = ErrorType.UNKNOWN_ERROR
        ))

        whenever(mockRepo.findAll()) doThrow RuntimeException("some error")

        val actual = subject.loadAllAlgorithms()
        assertThat(actual).isEqualTo(Failure(expected))
    }
}
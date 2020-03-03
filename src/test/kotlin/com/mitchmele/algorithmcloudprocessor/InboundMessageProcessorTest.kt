package com.mitchmele.algorithmcloudprocessor


import com.mitchmele.algorithmcloudprocessor.common.MongoDbProcessingException
import com.mitchmele.algorithmcloudprocessor.services.MongoClient
import com.mitchmele.algorithmcloudprocessor.services.MongoValidationService
import com.mitchmele.algorithmcloudprocessor.store.AlgorithmDomainModel
import com.mitchmele.algorithmcloudprocessor.store.Category
import com.mitchmele.algorithmcloudprocessor.store.Tag
import com.nhaarman.mockito_kotlin.*
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.Test
import org.springframework.integration.support.MessageBuilder
import org.springframework.messaging.Message

class InboundMessageProcessorTest {

    val mockMongoClient: MongoClient = mock()
    val mockValidator: MongoValidationService = mock()

    val subject = InboundMessageProcessor(mockMongoClient, mockValidator)

    val incomingAlgorithmDomainModel = AlgorithmDomainModel(
        name = "countDupes",
        codeSnippet = """
            fun countDupes(arr: Array<Int>): Int = arr.size - arr.distinct()
        """.trimIndent(),
        category = Category(
            categoryDescription = "EASY",
            difficultyLevel = 2,
            tags = listOf(Tag("Collections"))
        )
    )

    val incomingAlgorithmDomainModelDupe = AlgorithmDomainModel(name = "countDupes")

    @Test
    fun `process - should consume a message and invoke the mongo client`() {
        val inboundMessage = MessageBuilder
            .withPayload(incomingAlgorithmDomainModel)
            .setHeader("header1", "some value")
            .build()

        whenever(mockValidator.validate(any())) doReturn true
        val actual = subject.process(inboundMessage)
        verify(mockMongoClient).saveAlgorithm(any())
    }

    @Test
    fun `process - should not call saveAlgorithm if name already exists in DB`() {
        val firstMessage = MessageBuilder
            .withPayload(incomingAlgorithmDomainModel)
            .setHeader("header1", "some value")
            .build()

        whenever(mockValidator.validate(any())) doReturn false

        subject.process(firstMessage)
        verify(mockMongoClient, times(0)).saveAlgorithm(any())
    }

    @Test
    fun `process - should throw a MongoDbProcessingException if the mongo client throws and fails`() {

        val inboundMessage2 = incomingAlgorithmDomainModel.toMessage()

        whenever(mockValidator.validate(any())) doReturn true
        whenever(mockMongoClient.saveAlgorithm(any())) doThrow RuntimeException("something bad happened")

        assertThatThrownBy {
            subject.process(inboundMessage2)
        }
            .isInstanceOf(MongoDbProcessingException::class.java)
            .hasMessage("something bad happened")
    }


    private fun AlgorithmDomainModel.toMessage(): Message<*> {
        return MessageBuilder.withPayload(this).build()
    }
}
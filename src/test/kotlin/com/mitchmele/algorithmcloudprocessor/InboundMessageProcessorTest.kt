package com.mitchmele.algorithmcloudprocessor


import com.mitchmele.algorithmcloudprocessor.services.MongoClient
import com.mitchmele.algorithmcloudprocessor.store.AlgorithmDomainModel
import com.mitchmele.algorithmcloudprocessor.store.Category
import com.mitchmele.algorithmcloudprocessor.store.Tag
import com.nhaarman.mockito_kotlin.*
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.assertj.core.api.AssertionsForInterfaceTypes.assertThat
import org.junit.Test
import org.springframework.integration.support.MessageBuilder
import org.springframework.messaging.Message

class InboundMessageProcessorTest {

    val mockMongoClient: MongoClient = mock()

    val subject = InboundMessageProcessor(mockMongoClient)

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

    @Test
    fun `process - should consume a message and invoke the mongo client`() {
        val inboundMessage = MessageBuilder
            .withPayload(incomingAlgorithmDomainModel)
            .setHeader("header1", "some value")
            .build()

        val actual = subject.process(inboundMessage)
        verify(mockMongoClient).saveAlgorithm(any())
    }

    @Test
    fun `process - should throw an exception if the mongo client fails`() {

        val inboundBadMessage = incomingAlgorithmDomainModel.toMessage()

        whenever(mockMongoClient.saveAlgorithm(any())) doThrow RuntimeException("something bad happened")

        assertThatThrownBy {
            subject.process(inboundBadMessage)
        }
            .isInstanceOf(RuntimeException::class.java)
            .hasMessage("Error with processing")
    }


    private fun AlgorithmDomainModel.toMessage(): Message<*> {
        return MessageBuilder.withPayload(this).build()
    }
}
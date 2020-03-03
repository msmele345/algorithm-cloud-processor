package services

import assertk.all
import assertk.assertThat
import assertk.assertions.*
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.JsonMappingException
import com.fasterxml.jackson.databind.exc.MismatchedInputException
import com.mitchmele.algorithmcloudprocessor.common.MongoDbProcessingException
import com.nhaarman.mockito_kotlin.*
import org.assertj.core.api.Assertions
import org.junit.Test
import org.springframework.integration.handler.advice.AbstractRequestHandlerAdvice
import org.springframework.integration.transformer.MessageTransformationException
import org.springframework.messaging.Message
import org.springframework.messaging.support.MessageBuilder
import java.io.IOException

class MessageErrorAdviceTest : MessageErrorAdvice(
    messagingTemplate = mock(),
    errorQueue = mock(),
    mongoDbErrorQueue = mock()
) {

    @Test
    fun `doInvoke - should call executionCallback when invoked`() {
        val inputMessage = MessageBuilder
            .withPayload("some payload")
            .build()

        val mockCallBack: ExecutionCallback = mock()

        doInvoke(callback = mockCallBack, target = null, message = inputMessage)

        verify(mockCallBack).execute()
    }


    @Test
    fun `doInvoke - should return result of callback if there are no errors`() {
        val inputMessage = MessageBuilder
            .withPayload("some payload")
            .build()

        val mockCallBack: ExecutionCallback = mock()

        whenever(mockCallBack.execute()) doReturn "some positive result"

        val actual = doInvoke(callback = mockCallBack, target = null, message = inputMessage)

        assertThat(actual).all {
            isNotNull()
            isEqualTo("some positive result")
        }
    }

    @Test
    fun `doInvoke - should throw a MessageTransformation Exception if the callback fails`() {

        val inputMessage = MessageBuilder
            .withPayload("some payload")
            .setHeader("some header", "a useful value")
            .build()

        val mockCallBack = mock<ExecutionCallback>()

        whenever(mockCallBack.execute()) doAnswer {
            throw MessageTransformationException(
                "couldn't transform this",
                RuntimeException("some runtime exception")
            )
        }
        val actual = doInvoke(callback = mockCallBack, target = null, message = inputMessage)

        val captor = argumentCaptor<Message<*>>()

        verify(messagingTemplate).send(eq(errorQueue), captor.capture())

        assertThat(actual).isNull()

        assertThat {
            mockCallBack.execute()
        }.isFailure().hasMessage("couldn't transform this; nested exception is java.lang.RuntimeException: some runtime exception")

        assertThat(captor.firstValue.payload).all {
            isNotNull()
            isEqualTo("some payload")
        }
    }

    @Test
    fun `doInvoke - should return null and route the message to the errorQueue if the callback fails`() {

        val inputMessage = MessageBuilder
            .withPayload("some payload")
            .setHeader("header1", "useful header value")
            .setHeader("header2", "another useful header value")
            .build()

        val mockCallBack = mock<ExecutionCallback>()

        whenever(mockCallBack.execute()) doThrow
            MessageTransformationException(
                "couldn't transform this",
                IOException("some io exception")
            )


        val actual = doInvoke(callback = mockCallBack, target = null, message = inputMessage)

        val captor = argumentCaptor<Message<*>>()

        verify(messagingTemplate).send(eq(errorQueue), captor.capture())

        Assertions.assertThat(actual).isNull()
    }

    @Test
    fun `doInvoke - errorMessages should have condensed causes set in headers to pass to rabbit`() {
        val inputMessage = MessageBuilder
            .withPayload("bad message")
            .setHeader("header1", "test 2")
            .build()

        val mockCallBack = mock<ExecutionCallback>()

        whenever(mockCallBack.execute()) doAnswer {
            throw MessageTransformationException("error", IOException("some io"))
        }

        val expectedHeaderValue = "error; nested exception is java.io.IOException: some io"

        doInvoke(callback = mockCallBack, target = null, message = inputMessage)

        argumentCaptor<Message<*>>().let { captor ->
            verify(messagingTemplate).send(eq(errorQueue), captor.capture())

            val causeInHeader = captor.firstValue.headers["errorMessage"]
            assertThat(causeInHeader).isEqualTo(expectedHeaderValue)
        }
    }

    @Test
    fun `doInvoke - errorMessages that contain MongoDbProcessingExceptions should be routed to the mongoDb error queue`() {
        val inputMessage = MessageBuilder
            .withPayload("bad message")
            .setHeader("header1", "mongo error")
            .build()

        val mockCallBack: ExecutionCallback = mock {
            on { execute() } doAnswer {
                throw MessageTransformationException(
                    "error",
                    MongoDbProcessingException("Processing Error with MongoDb", RuntimeException("some bad happened when writing to mongo"))
                )
            }
        }

        doInvoke(mockCallBack, target = null, message = inputMessage)

        val captor = argumentCaptor<Message<*>>()

        verify(messagingTemplate).send(eq(mongoDbErrorQueue), captor.capture())
    }

    @Test
    fun `doInvoke - errorMessages that contain MongoDbProcessingExceptions should contain headers containing the underlying cause`() {

        val inputMessage = MessageBuilder
            .withPayload("bad message")
            .setHeader("header1", "mongo error")
            .build()

        val expectedCause = "some mongo error"
        val mockCallBack = mock<ExecutionCallback>()

        whenever(mockCallBack.execute()) doAnswer {
            throw MessageTransformationException("error", MongoDbProcessingException("some mongo error", RuntimeException("mongo failed")))
        }
        doInvoke(mockCallBack, target = null, message = inputMessage)

        argumentCaptor<Message<*>>().let { captor ->
            verify(messagingTemplate).send(eq(mongoDbErrorQueue), captor.capture())
            assertThat(captor.firstValue.headers["errorMessage"])
                .isEqualTo(expectedCause)
        }
    }
}
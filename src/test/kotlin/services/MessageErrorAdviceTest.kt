package services

import assertk.all
import assertk.assertThat
import assertk.assertions.*
import com.nhaarman.mockito_kotlin.*
import org.assertj.core.api.Assertions
import org.junit.Test
import org.springframework.integration.transformer.MessageTransformationException
import org.springframework.messaging.Message
import org.springframework.messaging.support.MessageBuilder
import java.io.IOException

class MessageErrorAdviceTest: MessageErrorAdvice(
    messagingTemplate = mock(),
    errorQueue = mock()
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

        whenever(mockCallBack.execute()) doAnswer  {
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
}
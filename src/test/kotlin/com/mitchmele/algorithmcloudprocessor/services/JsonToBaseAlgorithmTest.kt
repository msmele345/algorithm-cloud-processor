package com.mitchmele.algorithmcloudprocessor.services

import assertk.assertThat
import assertk.assertions.hasMessage
import assertk.assertions.isEqualTo
import assertk.assertions.isFailure
import assertk.assertions.isSuccess
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.mitchmele.algorithmcloudprocessor.store.BaseAlgorithm
import com.mitchmele.algorithmcloudprocessor.utils.UnitTest
import org.junit.Test
import org.junit.experimental.categories.Category
import org.springframework.integration.support.MessageBuilder

@Category(UnitTest::class)
class JsonToBaseAlgorithmTest {

    private val jsonJacksonMapper = jacksonObjectMapper()

    private fun jsonToBaseAlgorithmTransformer(
        jsonMapper: ObjectMapper = jsonJacksonMapper
    ) : JsonToBaseAlgorithmTransformer {
        return JsonToBaseAlgorithmTransformer(jsonMapper)
    }

    @Test
    fun `doTransform - provided valid json, should map the incoming message into a BaseAlgorithm`() {

        val inputJson = "{\"name\": \"reverseString\", \"codeSnippet\": \"fun reverseString(str: String) : String = str.reverse() \", \"category\": \"EASY\", \"isSolved\": true}\n"

        val inputMessage = MessageBuilder
            .withPayload(inputJson)
            .setHeader("some header", "header value")
            .build()

        val expected = BaseAlgorithm(
            name = "reverseString",
            codeSnippet = """
                fun reverseString(str: String) : String = str.reverse() 
            """.trimIndent(),
            category = "EASY",
            isSolved = true
        )

        val subject = jsonToBaseAlgorithmTransformer()

        subject.transform(inputMessage).let { actual ->
            assertThat { actual.payload }
                .isSuccess()
                .isEqualTo(expected)
        }
    }


    @Test
    fun `doTransform - should throw an exception and log the error if the transformer fails`() {

        val badJson = "{\"name\": \"reverseString\"sdfsd, \"codeSnippet\": \"fun reverseString(str: String) : String = str.reverse() \", \"category\": \"EASY\", \"isSolved\": true}\n"

        val inputMessage = MessageBuilder
            .withPayload(badJson)
            .setHeader("some header", "header value")
            .build()

        val subject = jsonToBaseAlgorithmTransformer()

        assertThat { subject.transform(inputMessage) }.isFailure()
            .hasMessage("failed to transform message; nested exception is com.fasterxml.jackson.core.JsonParseException: Unexpected character ('s' (code 115)): was expecting comma to separate Object entries\n" +
                " at [Source: (String)\"{\"name\": \"reverseString\"sdfsd, \"codeSnippet\": \"fun reverseString(str: String) : String = str.reverse() \", \"category\": \"EASY\", \"isSolved\": true}\n" +
                "\"; line: 1, column: 26]")
    }
}
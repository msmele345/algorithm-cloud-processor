package com.mitchmele.algorithmcloudprocessor

import assertk.all
import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isNotNull
import org.junit.Test
import org.springframework.integration.support.MessageBuilder

class InboundMessageProcessorTest {

    val subject = InboundMessageProcessor()

    @Test
    fun `process - should consume a message`() {
        val inboundMessage = MessageBuilder
            .withPayload("some message")
            .setHeader("header1", "some value")
            .build()

        val actual = 1 +1

        assertThat(actual).all {
            isNotNull()
            isEqualTo(1)
        }
    }
}

//Insures all of them get run even if the first fails
//assertAll {
//    assertThat(false).isTrue()
//    assertThat(true).isFalse()
//}
// -> The following 2 assertions failed:
//    - expected to be true
//    - expected to be false

//EXCEPTIONS:
//assertThat {
//    throw Exception("error")
//}.isFailure().hasMessage("wrong")
// -> expected [message] to be:<["wrong"]> but was:<["error"]>

//This method also allows you to assert on successfully returned values.
//
//assertThat { 1 + 1 }.isSucess().isNegative()
// -> expected to be negative but was:<2>
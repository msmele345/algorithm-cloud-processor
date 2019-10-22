package com.mitchmele.algorithmcloudprocessor

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.messaging.Message
import org.springframework.stereotype.Service
import java.util.concurrent.CountDownLatch

@Service
class InboundMessageProcessor {
    //eventuall will be mongo db client
    private val logger : Logger = LoggerFactory.getLogger(InboundMessageProcessor::class.java)

    private val latch: CountDownLatch = CountDownLatch(10)

    fun process(msg: Message<*>) {
        logger.info("received message='{}'", msg)
        logger.info("HANDLER")
        latch.countDown()
    }
}
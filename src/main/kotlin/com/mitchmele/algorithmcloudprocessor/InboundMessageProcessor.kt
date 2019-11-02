package com.mitchmele.algorithmcloudprocessor

import com.mitchmele.algorithmcloudprocessor.services.MongoClient
import com.mitchmele.algorithmcloudprocessor.store.AlgorithmDomainModel
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.messaging.Message
import org.springframework.stereotype.Service
import java.util.concurrent.CountDownLatch

@Service
class InboundMessageProcessor(
    private val mongoClient: MongoClient
) {
    private val logger: Logger = LoggerFactory.getLogger(InboundMessageProcessor::class.java)

    private val latch: CountDownLatch = CountDownLatch(1)

    fun process(msg: Message<*>) {
        logger.info("received message='{}'", msg)
        try {
            (msg.payload as AlgorithmDomainModel).let { algorithmDomainModel ->
                mongoClient.saveAlgorithm(algorithmDomainModel)
            }
        } catch (e: Exception) {
            throw RuntimeException("Error with processing")
        }
        latch.countDown()
    }
}
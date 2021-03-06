package com.mitchmele.algorithmcloudprocessor

import com.mitchmele.algorithmcloudprocessor.common.MongoDbProcessingException
import com.mitchmele.algorithmcloudprocessor.services.MongoClient
import com.mitchmele.algorithmcloudprocessor.services.MongoValidationService
import com.mitchmele.algorithmcloudprocessor.store.AlgorithmDomainModel
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.messaging.Message
import org.springframework.stereotype.Service
import java.util.concurrent.CountDownLatch

@Service
class InboundMessageProcessor(
    private val mongoClient: MongoClient,
    private val mongoValidationService: MongoValidationService
) {

    private val logger: Logger = LoggerFactory.getLogger(InboundMessageProcessor::class.java)
    private val latch: CountDownLatch = CountDownLatch(1)

    fun process(msg: Message<*>) {
        logger.info("received message='{}'", msg)
        val incomingAlgorithmDomainModel = msg.payload as AlgorithmDomainModel

        try {
            mongoValidationService.validate(incomingAlgorithmDomainModel).let { checkPasses ->
                when {
                    checkPasses -> {
                        mongoClient.saveAlgorithm(incomingAlgorithmDomainModel)
                    }
                    else -> {
                        logger.info("did not write duplicate message='{}'", msg.payload)
                    }
                }
            }

        } catch (e: Exception) {
            throw MongoDbProcessingException(e.localizedMessage, e)
        }
        latch.countDown()
    }
}
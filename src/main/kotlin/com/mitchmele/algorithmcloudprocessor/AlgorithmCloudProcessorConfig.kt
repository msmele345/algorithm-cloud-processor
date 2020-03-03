package com.mitchmele.algorithmcloudprocessor

import com.mitchmele.algorithmcloudprocessor.services.AlgorithmTransformer
import com.mitchmele.algorithmcloudprocessor.services.JsonToBaseAlgorithmTransformer
import com.rabbitmq.client.impl.AMQBasicProperties
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.cloud.stream.annotation.EnableBinding
import org.springframework.cloud.stream.messaging.Sink
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.integration.channel.DirectChannel
import org.springframework.integration.config.EnableIntegration
import org.springframework.integration.core.MessagingTemplate
import org.springframework.integration.dsl.IntegrationFlow
import org.springframework.integration.dsl.IntegrationFlows
import org.springframework.messaging.Message
import org.springframework.messaging.MessageChannel
import org.springframework.messaging.MessageHandler
import services.MessageErrorAdvice
import java.util.*


@Configuration
@EnableIntegration
@EnableBinding(AlgorithmCloudBinder::class)
@ComponentScan("com.mitchmele.*")
class AlgorithmCloudProcessorConfig {

    @Bean
    @Qualifier("errorQueue")
    internal fun errorQueue() = DirectChannel()

    @Bean
    @Qualifier("mongoDbErrorQueue")
    internal fun mongoDbErrorQueue() = DirectChannel()

    @Bean
    internal fun messagingTemplate(): MessagingTemplate {
        return MessagingTemplate()
    }

    @Bean("messageHandler")
    internal fun algorithmMessageHandler(
        processor: InboundMessageProcessor
    ): MessageHandler = MessageHandler { message -> processor.process(message) }

    @Bean
    internal fun kafkaListenerFlow(
        @Qualifier("messageHandler") algorithmMessageHandler: MessageHandler,
        messageErrorAdvice: MessageErrorAdvice,
        jsonToBaseAlgorithmTransformer: JsonToBaseAlgorithmTransformer,
        algorithmTransformer: AlgorithmTransformer
    ): IntegrationFlow {
        return IntegrationFlows
            .from(Sink.INPUT)
            .transform(jsonToBaseAlgorithmTransformer, java.util.function.Consumer { e -> e.advice(messageErrorAdvice).requiresReply(false) })
            .filter(Objects::nonNull)
            .transform(algorithmTransformer, java.util.function.Consumer { e -> e.advice(messageErrorAdvice).requiresReply(false) })
            .handle(algorithmMessageHandler) { e ->
                e.advice(messageErrorAdvice).requiresReply(false)
            }
            .get()
    }

    @Bean
    internal fun messageErrorAdvice(
        @Qualifier("errorQueue") errorQueue: MessageChannel,
        @Qualifier("mongoDbErrorQueue") mongoDbErrorQueue: MessageChannel,
        messagingTemplate: MessagingTemplate
    ): MessageErrorAdvice {
        return MessageErrorAdvice(messagingTemplate, errorQueue, mongoDbErrorQueue)
    }

    @Bean
    internal fun errorFlow(): IntegrationFlow {
        return IntegrationFlows
            .from("errorQueue")
            .log<Any> {
                "Sending Message to ErrorQueue. ErrorMessage:" +
                    " ${it.headers["errorMessage"].toString()}, Headers: ${it.headers}"
            }
            .channel(AlgorithmCloudBinder.OUTPUT)
            .get()
    }

    @Bean
    internal fun mongoErrorFlow(): IntegrationFlow {
        return IntegrationFlows
            .from("mongoDbErrorQueue")
            .log<Any> {
                "Sending Message to MongoErrorQueue. ErrorMessage:" +
                    " ${it.headers["errorMessage"].toString()}, Headers: ${it.headers}"
            }
            .channel(AlgorithmCloudBinder.DB_ERROR_OUTPUT)
            .get()

    }
}
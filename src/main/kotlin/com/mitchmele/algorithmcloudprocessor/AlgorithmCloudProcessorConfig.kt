package com.mitchmele.algorithmcloudprocessor

import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.cloud.stream.annotation.EnableBinding
import org.springframework.cloud.stream.messaging.Sink
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.integration.annotation.ServiceActivator
import org.springframework.integration.channel.DirectChannel
import org.springframework.integration.config.EnableIntegration
import org.springframework.integration.core.MessagingTemplate
import org.springframework.integration.dsl.IntegrationFlow
import org.springframework.integration.dsl.IntegrationFlows
import org.springframework.messaging.Message
import org.springframework.messaging.MessageChannel
import org.springframework.messaging.MessageHandler
import org.springframework.messaging.support.GenericMessage
import services.MessageErrorAdvice


@Configuration
@EnableIntegration
@EnableBinding(AlgorithmCloudBinder::class)
@ComponentScan("com.mitchmele.*")
class AlgorithmCloudProcessorConfig {

    @Bean
    @Qualifier("errorQueue")
    internal fun errorQueue() = DirectChannel()

    @Bean
    internal fun messagingTemplate(): MessagingTemplate {
        return MessagingTemplate()
    }

    @Bean("messageHandler")
    internal fun algorithmMessageHandler(
        processor: InboundMessageProcessor
    ): MessageHandler = MessageHandler { message ->
        processor.process(message)
    }

    @Bean
    internal fun kafkaListenerFlow(
        @Qualifier("messageHandler") algorithmMessageHandler: MessageHandler,
        messageErrorAdvice: MessageErrorAdvice
    ): IntegrationFlow {
        return IntegrationFlows
            .from(Sink.INPUT)
            .handle(algorithmMessageHandler) { e ->
                e.advice(messageErrorAdvice).requiresReply(false)
            }
            .get()
    }

    @Bean
    internal fun messageErrorAdvice(
        @Qualifier("errorQueue") errorQueue: MessageChannel,
        messagingTemplate: MessagingTemplate
    ): MessageErrorAdvice {
        return MessageErrorAdvice(messagingTemplate, errorQueue)
    }

    @Bean
    internal fun errorFlow(): IntegrationFlow {
        return IntegrationFlows
            .from("errorQueue")
            .log<Any> { message ->
                println("EVENT ERROR: $message")
                println("EVENT ERROR PAYLOAD: ${message.payload}")
                println("EVENT ERROR HEADERS: ${message.headers}")
            }
            .get()
    }
}
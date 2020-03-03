package services

import com.fasterxml.jackson.databind.JsonMappingException
import com.mitchmele.algorithmcloudprocessor.common.MongoDbProcessingException
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.integration.core.MessagingTemplate
import org.springframework.integration.handler.advice.AbstractRequestHandlerAdvice
import org.springframework.integration.transformer.MessageTransformationException
import org.springframework.messaging.Message
import org.springframework.messaging.MessageChannel
import org.springframework.messaging.support.MessageBuilder
import org.springframework.stereotype.Service
import java.io.IOException

@Service
class MessageErrorAdvice(
    val messagingTemplate: MessagingTemplate,
    val errorQueue: MessageChannel,
    @Qualifier("mongoDbErrorQueue")
    val mongoDbErrorQueue: MessageChannel
) : AbstractRequestHandlerAdvice() {
    override fun doInvoke(callback: ExecutionCallback, target: Any?, message: Message<*>): Any? {
        return try {
            callback.execute()
        } catch (ex: MessageTransformationException) {
            ex.cause?.let { transformationCause -> //gives flexibility to throw custom exceptions and route to different error channels based on type
                when (transformationCause) {
                    is IOException -> {
                        val errorMessage = MessageBuilder
                            .withPayload(message.payload)
                            .copyHeadersIfAbsent(message.headers)
                            .setHeader("errorMessage", ex.localizedMessage)
                            .build()
                        messagingTemplate.send(errorQueue, errorMessage)
                    }
                    is MongoDbProcessingException -> {
                        val errorMessage = MessageBuilder
                            .withPayload(message.payload)
                            .copyHeadersIfAbsent(message.headers)
                            .setHeader("errorMessage", ex.localizedMessage.parseLocalizedMessage())
                            .build()
                        messagingTemplate.send(mongoDbErrorQueue, errorMessage)
                    }
                    else -> {
                        val errorMessage = MessageBuilder
                            .withPayload(message.payload)
                            .copyHeadersIfAbsent(message.headers)
                            .setHeader("errorMessage", ex.localizedMessage)
                            .build()
                        messagingTemplate.send(errorQueue, errorMessage)
                    }
                }
            }
            null
        } catch (ex: ThrowableHolderException) {
            val errorMessage = MessageBuilder
                .withPayload(message.payload)
                .copyHeadersIfAbsent(message.headers)
                .setHeader("errorMessage", ex.localizedMessage)
                .build()
            messagingTemplate.send(errorQueue, errorMessage)
        }
    }
}

fun String.parseLocalizedMessage(): String {
    return this.split(":").drop(1).joinToString().trimStart()
}
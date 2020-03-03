package com.mitchmele.algorithmcloudprocessor.services

import com.fasterxml.jackson.core.JsonParseException
import com.fasterxml.jackson.databind.JsonMappingException
import com.fasterxml.jackson.databind.ObjectMapper
import com.mitchmele.algorithmcloudprocessor.store.BaseAlgorithm
import com.mitchmele.algorithmcloudprocessor.utils.payloadAsString
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.integration.transformer.AbstractTransformer
import org.springframework.integration.transformer.MessageTransformationException
import org.springframework.messaging.Message
import org.springframework.stereotype.Service
import java.io.IOException

@Service
class JsonToBaseAlgorithmTransformer(
    private val jsonMapper: ObjectMapper
) : AbstractTransformer() {
    override fun doTransform(message: Message<*>?): Any? {
        val logger: Logger = LoggerFactory.getLogger(JsonToBaseAlgorithmTransformer::class.java)

        return message?.let { message ->
            val payload = message.payloadAsString()
            try {
                jsonMapper.readValue(payload, BaseAlgorithm::class.java)

            } catch (jpe: JsonParseException) {
                logger.info("JsonParseException", jpe)
                throw jpe
            } catch (jme: JsonMappingException) {
                throw jme

            } catch (ioe: IOException) {
                throw ioe

            } catch (ex: MessageTransformationException) {
                throw ex
            }
        }
    }
}
/*
fun Message<*>.payloadAsString() =
    when {
        this.payload is ByteArray -> String(this.payload as ByteArray)
        this.payload is String -> this.payload as String
        else -> this.payload.toString()
    }
 */
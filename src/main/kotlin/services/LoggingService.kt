package services

import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.slf4j.Logger
import org.slf4j.MDC
import java.io.StringWriter
import javax.xml.bind.JAXBContext
import javax.xml.bind.Marshaller

open class LoggingService(
    val logginOn: Boolean = false,
    val logger: Logger
) {

    open fun logDebugLevel(value: String) {
        if (logginOn) {
            System.lineSeparator().let { ls ->
                logger.info("${"/".repeat(20)}$ls$value$ls${"\\".repeat(20)}")
            }
        }
    }

    open fun logInfoLevel(value: String, serviceName: String) {
        if (logginOn) {
            logger.info("${"*".repeat(20)} $serviceName ${"*".repeat(20)}")

            System.lineSeparator().let { ls ->
                logger.info("${"/".repeat(20)}$ls$value$ls${"\\".repeat(20)}")
            }
        }
    }

    open fun logError(exception: Exception, serviceName: String) {
        if (logginOn) {
            logger.info("${"*".repeat(20)} $serviceName ${"*".repeat(20)}")
            logger.error(exception.localizedMessage + exception.stackTrace.joinToString(separator = System.lineSeparator(),
                prefix = System.lineSeparator(),
                postfix = "${System.lineSeparator()}${"/".repeat(20)}"))
        }
    }

    open fun logError(message: String, serviceName: String, signature: String = "") {
        if (logginOn) {
            logger.info("${"*".repeat(20)} $serviceName - $signature ${"*".repeat(20)}")
            logger.error(message)
        }
    }

    open fun logXML(clazz: Any, serviceName: String) {
        if (logginOn) {
            logger.info("${"*".repeat(20)} $serviceName ${"*".repeat(20)}")
            try {
                logDebugLevel(StringWriter().let { sw ->
                    JAXBContext.newInstance(clazz::class.java).createMarshaller().run {
                        setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true)
                        marshal(clazz, sw)
                    }
                    sw.toString()
                })
            } catch (ex: Exception) {
                logDebugLevel("Could not marshal due to: ${ex.localizedMessage}")
            }
        }
    }

    open fun logJson(clazz: Any, serviceName: String) {
        if (logginOn) {
            logger.info("${"*".repeat(20)} $serviceName ${"*".repeat(20)}")
            logDebugLevel(jacksonObjectMapper().run {
                enable(SerializationFeature.INDENT_OUTPUT)
                writeValueAsString(clazz)
            })
        }
    }

    open fun logError(serviceName: String, payload: String, signature: String, exception: Throwable?) {
        MDC.clear()
        MDC.put("Signature", signature)
        MDC.put("Service Name", serviceName)
        logger.error(payload, exception)
    }


}
package com.mitchmele.algorithmcloudprocessor

import org.springframework.cloud.stream.annotation.Input
import org.springframework.cloud.stream.annotation.Output
import org.springframework.cloud.stream.messaging.Sink
import org.springframework.integration.channel.DirectChannel
import org.springframework.integration.channel.QueueChannel
import org.springframework.messaging.MessageChannel
import org.springframework.messaging.SubscribableChannel

interface AlgorithmCloudBinder {

    @Input(Sink.INPUT)
    fun input(): SubscribableChannel

    @Output(Companion.OUTPUT)
    fun output(): MessageChannel

    @Output(Companion.ERRORS)
    fun errorQueue(): MessageChannel

    @Output(Companion.DB_ERRORS)
    fun mongoDbErrorQueue(): MessageChannel

    @Output(DB_ERROR_OUTPUT)
    fun dbErrorOutput(): MessageChannel

    companion object {
        const val OUTPUT = "output"
        const val ERRORS = "errorQueue"
        const val DB_ERRORS = "mongoDbErrorQueue"
        const val DB_ERROR_OUTPUT = "dbErrorOutput"
    }
}
package com.mitchmele.algorithmcloudprocessor.utils

import org.springframework.messaging.Message

fun Message<*>.payloadAsString() =
    when {
        this.payload is ByteArray -> String(this.payload as ByteArray)
        this.payload is String -> this.payload as String
        else -> this.payload.toString()
    }
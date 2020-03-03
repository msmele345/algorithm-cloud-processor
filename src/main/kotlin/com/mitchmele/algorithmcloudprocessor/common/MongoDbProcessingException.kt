package com.mitchmele.algorithmcloudprocessor.common

class MongoDbProcessingException(message: String, throwable: Throwable? = null): Throwable(message, throwable)

data class DatabaseError(
    val message: String?,
    val cause: Throwable?
)
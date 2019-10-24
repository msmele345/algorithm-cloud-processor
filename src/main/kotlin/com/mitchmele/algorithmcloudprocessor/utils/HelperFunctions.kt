package com.mitchmele.algorithmcloudprocessor.utils

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.databind.util.StdDateFormat
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import com.mitchmele.algorithmcloudprocessor.store.BaseAlgorithm

val jsonMapper = ObjectMapper().apply {
    registerKotlinModule()
    disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
    dateFormat = StdDateFormat()
}


fun createBaseAlgorithm(algorithm: String): BaseAlgorithm {
    return jacksonObjectMapper().run {
        readValue(algorithm, BaseAlgorithm::class.java)
    }
}
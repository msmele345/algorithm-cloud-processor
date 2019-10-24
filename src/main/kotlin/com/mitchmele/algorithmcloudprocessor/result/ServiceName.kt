package com.mitchmele.algorithmcloudprocessor.result


enum class ServiceName(val value: String) {
    MONGO("MONGO DB"),
    UNKNOWN("UNKNOWN ERROR");

    companion object {
        fun from(s: String) {
            values().find { serviceName -> serviceName.value == s.toUpperCase() }
            ?.let { serviceName -> serviceName } ?: UNKNOWN
        }
    }
}
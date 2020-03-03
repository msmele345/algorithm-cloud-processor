package com.mitchmele.algorithmcloudprocessor.common

import kotlin.test.fail


fun <SuccessType, FailureType> Result<SuccessType, FailureType>.succeeds(): SuccessType {
    return when (this) {
        is Success -> value
        is Failure -> fail()
    }
}

infix fun <SuccessType, FailureType> Result<SuccessType, FailureType>.succeedsAnd(onSuccess: (SuccessType) -> Unit) {
    return when (this) {
        is Success -> onSuccess(value)
        is Failure -> fail()
    }
}

fun <SuccessType, FailureType> Result<SuccessType, FailureType>.fails(): FailureType {
    return when (this) {
        is Success -> fail()
        is Failure -> value
    }
}

infix fun <SuccessType, FailureType> Result<SuccessType, FailureType>.failsAnd(onFailure: (FailureType) -> Unit) {
    return when (this) {
        is Success -> fail()
        is Failure -> onFailure(value)
    }
}

infix fun <SuccessType, FailureType, ExpectedFailureType> Result<SuccessType, FailureType>.failsWith(expectedFailureType: ExpectedFailureType) {
    return when (this) {
        is Success -> fail()
        is Failure -> assert(this.value == expectedFailureType)
    }
}
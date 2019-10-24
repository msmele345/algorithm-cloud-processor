package com.mitchmele.algorithmcloudprocessor.result


sealed class Result<out SuccessType, out FailureType>

inline fun <SuccessType, NewSuccessType, FailureType>
    Result<SuccessType, FailureType>.mapSuccess(transform: (SuccessType) -> NewSuccessType)
    : Result<NewSuccessType, FailureType> {
    return when (this) {
        is Success -> Success(transform(this.value))
        is Failure -> this
    }
}

inline fun <SuccessType, FailureType, NewFailureType>
    Result<SuccessType, FailureType>.mapFailure(transform: (FailureType) -> NewFailureType)
    : Result<SuccessType, NewFailureType> {
    return when (this) {
        is Success -> this
        is Failure -> Failure(transform(this.value))
    }
}

inline fun <SuccessType, FailureType> Result<SuccessType, FailureType>.getOrElse(
    transform: (FailureType) -> SuccessType
): SuccessType {
    return when (this) {
        is Success -> value
        is Failure -> transform(this.value)
    }
}


inline fun <SuccessType, NewSuccessType, FailureType>
    Result<SuccessType, FailureType>.flatMap(transform: (SuccessType) -> Result<NewSuccessType, FailureType>)
    : Result<NewSuccessType, FailureType> {
    return when (this) {
        is Success -> transform(this.value)
        is Failure -> this
    }
}

data class Success<out SuccessType>(val value: SuccessType) : Result<SuccessType, Nothing>()
data class Failure<out FailureType>(val value: FailureType) : Result<Nothing, FailureType>()

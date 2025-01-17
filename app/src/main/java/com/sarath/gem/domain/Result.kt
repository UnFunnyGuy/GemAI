package com.sarath.gem.domain

typealias GemError = Error

sealed interface Result<out D, out E : GemError> {
    data class Success<out D, out E : GemError>(val data: D) : Result<D, E>

    data class Error<out D, out E : GemError>(val error: E) : Result<D, E>
}

inline fun <D, E : Error, R> Result<D, E>.map(map: (D) -> R): Result<R, E> {
    return when (this) {
        is Result.Error -> Result.Error(error)
        is Result.Success -> Result.Success(map(data))
    }
}

fun <D, E : Error> Result<D, E>.asEmptyDataResult(): EmptyResult<E> {
    return map {}
}

inline fun <D, E : Error> Result<D, E>.onSuccess(action: (D) -> Unit): Result<D, E> {
    return when (this) {
        is Result.Error -> this
        is Result.Success -> {
            action(data)
            this
        }
    }
}

inline fun <D, E : Error> Result<D, E>.onError(action: (E) -> Unit): Result<D, E> {
    return when (this) {
        is Result.Error -> {
            action(error)
            this
        }
        is Result.Success -> this
    }
}

typealias EmptyResult<E> = Result<Unit, E>

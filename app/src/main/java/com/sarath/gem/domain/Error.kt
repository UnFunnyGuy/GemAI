package com.sarath.gem.domain

sealed interface Error

sealed interface RequestError : Error {
    data class Generic(val message: String) : RequestError

    data object ApiKeyError : RequestError

    enum class Network : RequestError {
        NO_INTERNET,
        TIMEOUT,
        SERVER_ERROR,
        SERIALIZATION,
        UNKNOWN,
    }
}

package com.sarath.gem.core.util

import kotlinx.coroutines.withTimeoutOrNull

/**
 * Executes a suspendable block with a timeout and optional error handling.
 *
 * This function attempts to execute the provided `block` within a specified `timeout`. If the block completes
 * successfully within the timeout, its result is returned. If the block throws an exception or exceeds the timeout, the
 * `onError` handler (if provided) is invoked with the exception, and `null` is returned.
 *
 * @param timeout The timeout in milliseconds for the execution of the `block`. Defaults to 15000ms.
 * @param onError An optional lambda function to handle any exceptions thrown by the `block` or timeout exceptions. It
 *   receives the exception as a parameter.
 * @param block The suspendable lambda function to be executed.
 * @return The result of the `block` if it completes successfully within the timeout, otherwise `null`.
 */
suspend fun <T> suspendedSafeTry(
    timeout: Long = 15000L,
    onError: ((Throwable) -> Unit)? = null,
    block: suspend () -> T,
): T? {
    return try {
        withTimeoutOrNull(timeout) { block() }
    } catch (e: Throwable) {
        onError?.invoke(e)
        null
    }
}

/**
 * Executes the given [block] of code and returns its result if successful. If an exception is thrown during the
 * execution of the block, the [onError] callback is invoked with the exception, and the function returns null.
 *
 * @param onError A callback function that is invoked with the caught exception if an error occurs. Defaults to null.
 * @param block The code block to be executed.
 * @param <T> The type of the result returned by the [block].
 * @return The result of the [block] if successful, otherwise null.
 */
fun <T> safeTry(onError: ((Throwable) -> Unit)? = null, block: () -> T): T? {
    return try {
        block()
    } catch (e: Throwable) {
        onError?.invoke(e)
        null
    }
}

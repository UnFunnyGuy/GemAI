package com.sarath.gem.core.base

import kotlinx.coroutines.flow.Flow

/**
 * Base interface for Use Cases in the application. A Use Case represents an action or operation that the application
 * can perform.
 *
 * This interface defines three methods for performing the Use Case:
 * - `perform()`: Executes the Use Case without any input parameters and returns a result of type `Result`.
 * - `perform(params: Params)`: Executes the Use Case with input parameters of type `Params` and returns a nullable
 *   result of type `Result`.
 * - `performStreaming(params: Params? = null)`: Executes the Use Case and returns a stream of results of type `Result`
 *   as a [Flow]. It can optionally accept input parameters of type `Params`.
 *
 * Implementations of this interface should override at least one of these methods to provide the specific logic for the
 * Use Case.
 *
 * @param Params The type of input parameters for the Use Case.
 * @param Result The type of result returned by the Use Case.
 */
interface BaseUseCase<in Params, out Result> {

    /**
     * Executes the use case.
     *
     * This is the base implementation of the `perform` function, which throws a `NotImplementedError`. Subclasses
     * should override this function to provide their own implementation of the use case logic.
     *
     * @return A [Result] representing the outcome of the use case execution.
     * @throws NotImplementedError If the function is not overridden in a subclass.
     */
    suspend fun perform(): Result = throw NotImplementedError("BaseUseCase perform() not implemented")

    /**
     * Executes the use case with the given [params].
     *
     * This is the main function to be overridden by concrete use case classes. It should contain the core business
     * logic of the use case.
     *
     * @param params The input parameters for the use case.
     * @return The result of the use case execution, or null if the use case doesn't return a result.
     * @throws NotImplementedError If the base implementation is called directly without being overridden.
     */
    suspend fun perform(params: Params): Result? =
        throw NotImplementedError("BaseUseCase perform(params) not implemented")

    /**
     * Performs a streaming operation based on the provided [params].
     *
     * This function should be overridden in concrete UseCase implementations to define the actual streaming logic. The
     * base implementation throws a NotImplementedError.
     *
     * @param params Optional parameters for the operation.
     * @return A [Flow] emitting [Result] objects representing the stream of results.
     * @throws NotImplementedError If called on the base UseCase class.
     */
    fun performStreaming(params: Params): Flow<Result> =
        throw NotImplementedError("BaseUseCase performStreaming() not implemented")

    fun performStreaming(): Flow<Result> = throw NotImplementedError("BaseUseCase performStreaming() not implemented")
}

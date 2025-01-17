package com.sarath.gem.core.ai

import com.google.ai.client.generativeai.GenerativeModel
import com.sarath.gem.domain.model.AIModel
import com.sarath.gem.domain.repository.DatastoreRepository
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

abstract class BaseAIModel(private val datastoreRepository: DatastoreRepository) {

    /**
     * A [CoroutineExceptionHandler] that logs any uncaught exceptions that occur within coroutines launched in the
     * scope.
     */
    private val exceptionHandler = CoroutineExceptionHandler { _, exception ->
        println("GemAIModel Exception:: $exception")
    }

    /** Coroutine scope for managing background operations */
    protected val coroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Default + exceptionHandler)

    /** Name of the generative AI model */
    protected var modelName: String = AIModel.GEMINI_1_5_FLASH.modelName
        private set

    /** API key for accessing the Gemini API (default key is temporary) */
    protected var apiKey: String = "AIzaSyAy5BO5bOFOSLtAExAEBz1irLyAIsfFfoI"
        private set

    /** Builder for creating the generative AI model */
    protected val modelBuilder = ModelBuilder.Builder()

    /** Lazy-loaded instance of the generative AI model */
    protected val geminiAIModel: GenerativeModel by lazy { getGenerativeModel() }

    /** Initializes the class by fetching configuration settings asynchronously. */
    init {
        coroutineScope.launch {
            val deferredModelName = async { datastoreRepository.getModel().modelName }
            val deferredApiKey = async { datastoreRepository.getApiKey() }
            modelName = deferredModelName.await()
            deferredApiKey.await()?.let { apiKey = it }
        }
    }

    /**
     * Creates and configures the generative AI model instance.
     *
     * @return The initialized [GenerativeModel].
     * @throws IllegalStateException If the API key is not set.
     */
    protected open fun getGenerativeModel(): GenerativeModel {
        // check(apiKey.isNotBlank()) { "API key must be initialized before creating the model" }
        return modelBuilder.setApiKey(apiKey).setModel(modelName).build()
    }

    /**
     * Tests the validity of a given key.
     *
     * This function delegates the key validation to the `modelBuilder.testKey()` function.
     *
     * @param key The key to be tested.
     * @return `true` if the key is valid, `false` otherwise.
     */
    suspend fun testKey(key: String): Boolean {
        return modelBuilder.testKey(key)
    }

    /** Closes the [GemAIModel], canceling all ongoing operations. needed for future use */
    fun close() {
        coroutineScope.cancel()
    }
}

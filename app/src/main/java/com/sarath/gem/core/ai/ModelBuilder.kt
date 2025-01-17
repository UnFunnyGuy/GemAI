package com.sarath.gem.core.ai

import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.BlockThreshold
import com.google.ai.client.generativeai.type.Content
import com.google.ai.client.generativeai.type.GenerationConfig
import com.google.ai.client.generativeai.type.HarmCategory
import com.google.ai.client.generativeai.type.SafetySetting
import com.google.ai.client.generativeai.type.TextPart
import com.google.ai.client.generativeai.type.generationConfig
import com.sarath.gem.domain.model.AIModel

/**
 * A class to construct and configure instances of a generative AI model.
 *
 * This builder pattern enables flexibility in customizing the model by allowing developers to:
 * - Set API keys for authentication.
 * - Specify model names.
 * - Configure generation settings such as temperature, top-p, and top-k.
 * - Apply safety settings to handle sensitive or harmful content.
 * - Provide system instructions to control the model's behavior and tone.
 *
 * ### Usage Example:
 * ```
 * val model = ModelBuilder.Builder()
 *     .setApiKey("your-api-key")
 *     .setModel("gemini-2.0")
 *     .setConfig {
 *         temperature = 0.8f
 *         topP = 0.95f
 *     }
 *     .addInstruction("Always respond in markdown.")
 *     .build()
 * ```
 *
 * The resulting `GenerativeModel` instance is fully configured and ready for use.
 *
 * @property apiKey The API key used to authenticate requests.
 * @property modelName The name of the generative model to use.
 * @property generationConfig Configuration for controlling the text generation process.
 * @property safetySettings A list of safety settings to manage content moderation.
 * @property systemInstructions A list of system instructions to guide the model's behavior.
 */
class ModelBuilder
private constructor(
    val apiKey: String,
    val modelName: String,
    val generationConfig: GenerationConfig,
    val safetySettings: List<SafetySetting>,
    val systemInstructions: List<String>,
) {

    /** Builder class for constructing a [ModelBuilder] instance. */
    class Builder {

        private var apiKey: String = ""
        private var modelName: String = ""
        private var generationConfig = generationConfig {
            temperature = 0.6f
            topP = 0.8f
            topK = 30
            stopSequences = listOf("End of response", "STOP")
        }
        private var safetySettings = listOf(SafetySetting(HarmCategory.SEXUALLY_EXPLICIT, BlockThreshold.NONE))
        private var systemInstructions: MutableList<String> =
            mutableListOf(
                """
                    **Guidelines for the AI Assistant**

                    1. **Accuracy and Clarity:**
                        - Provide correct and precise information.
                        - Be concise and avoid unnecessary verbosity.
                        - Clarify ambiguities and provide only relevant information.
                        - Tailor responses to the user's level of expertise.

                    2. **Effective Communication:**
                        - Use grammatically correct and easy-to-understand language.
                        - Structure responses logically and coherently.
                        - Use appropriate tone and style based on the context.

                    3. **Comprehensive Responses:**
                        - Address all aspects of the user's query thoroughly.
                        - Provide sufficient context and background information.
                        - Offer multiple perspectives or solutions when applicable.

                    4. **User-Friendly Output:**
                        - **Utilize Markdown effectively:** 
                            - Use backticks (`) for inline code.
                            - Use triple backticks (```) for code blocks (e.g., ```python```, ```kotlin```).
                            - Use bullet points (`-`) and numbered lists (`1., 2., 3.`) for lists.
                            - Use headers (`###`, `####`) to organize content.
                            - Separate sections with blank lines.
                            - **Present information to the user in a clear and visually appealing manner using Markdown.**
                            - The style should be consistent and easy to read.
                            - **Markdown enhances readability and improves the user experience.** 
                        - Consider using visual aids (e.g., images, diagrams) where relevant. 
                        - Ensure output is easily accessible and understandable by the user.

                    5. **Technical Responses:**
                        - Include well-commented code examples.
                        - Explain the logic behind the code.
                        - Highlight potential issues and best practices.

                    6. **Handling Complex Topics:**
                        - Break down complex topics into smaller, digestible parts.
                        - Use examples and scenarios to illustrate concepts.
                        - Address potential user questions and suggest further resources.

                    7. **Continuous Learning:**
                        - Learn from user interactions and feedback.
                        - Identify areas for improvement and adapt accordingly.
                        - Stay updated on the latest information and trends.

                    **End of response.*
             """
                    .trimIndent()
            )

        /**
         * Sets the API key for the model.
         *
         * @param apiKey The API key to be used for authentication.
         * @return The [Builder] instance for chaining.
         */
        fun setApiKey(apiKey: String) = apply { this.apiKey = apiKey }

        /**
         * Sets the name of the generative AI model.
         *
         * @param modelName The name of the model to use.
         * @return The [Builder] instance for chaining.
         */
        fun setModel(modelName: String) = apply { this.modelName = modelName }

        /**
         * Configures generation settings using a lambda function.
         *
         * @param configure A lambda to configure [GenerationConfig].
         * @return The [Builder] instance for chaining.
         */
        fun setConfig(configure: GenerationConfig.Builder.() -> Unit) = apply {
            generationConfig = generationConfig(configure)
        }

        /**
         * Sets the safety settings for content moderation.
         *
         * @param settings A list of [SafetySetting] objects.
         * @return The [Builder] instance for chaining.
         */
        fun setSafetySettings(settings: List<SafetySetting>) = apply { safetySettings = settings }

        /**
         * Adds a system instruction to the list of instructions.
         *
         * @param instruction The instruction string to add.
         * @return The [Builder] instance for chaining.
         */
        fun addInstruction(instruction: String) = apply { systemInstructions.add(instruction.trimIndent()) }

        /**
         * Replaces all system instructions with the given list.
         *
         * @param instructions A list of instruction strings.
         * @return The [Builder] instance for chaining.
         */
        fun instructions(instructions: List<String>) = apply {
            systemInstructions.clear()
            systemInstructions.addAll(instructions.map { it.trimIndent() })
        }

        /**
         * Builds and returns a [GenerativeModel] instance with the configured settings.
         *
         * @return The configured [GenerativeModel].
         * @throws IllegalArgumentException If required fields like `apiKey` or `modelName` are blank.
         */
        fun build(): GenerativeModel {
            require(apiKey.isNotBlank()) { "API key must not be blank" }
            require(modelName.isNotBlank()) { "Model name must not be blank" }
            return GenerativeModel(
                apiKey = apiKey,
                modelName = modelName,
                generationConfig = generationConfig,
                safetySettings = safetySettings,
                systemInstruction = getSystemInstruction(),
            )
        }

        /**
         * Tests the validity of a given API key by attempting to generate a simple text response.
         *
         * This function initializes a GenerativeModel with the provided API key and attempts to generate content. If
         * the content generation is successful, it indicates a valid key, and the function returns `true`. If an
         * exception occurs during the process, it's assumed the key is invalid, the exception is printed to the
         * console, and the function returns `false`.
         *
         * @param key The API key to be tested.
         * @return `true` if the API key is valid, `false` otherwise.
         */
        suspend fun testKey(key: String): Boolean {
            return try {
                GenerativeModel(apiKey = key, modelName = AIModel.GEMINI_1_5_FLASH.modelName)
                    .generateContent("Hi, how are you?")
                true
            } catch (e: Exception) {
                e.printStackTrace()
                false
            }
        }

        /**
         * Converts system instructions into the required [Content] format.
         *
         * @return A [Content] instance containing the system instructions.
         */
        private fun getSystemInstruction(): Content {
            return Content(role = "system", parts = systemInstructions.map { TextPart(it) })
        }
    }
}

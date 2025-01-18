package com.sarath.gem.core.ai

import android.util.Log
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.generationConfig
import com.sarath.gem.core.base.PromptIcon
import com.sarath.gem.data.remote.model.PromptDto
import com.sarath.gem.domain.repository.DatastoreRepository
import kotlinx.serialization.json.Json
import javax.inject.Inject

/**
 * A specialized AI model for handling various system-level AI tasks within the application.
 *
 * This model is designed to perform specific AI operations such as generating chat titles from user prompts and
 * suggesting initial prompts based on past user interactions. It leverages a base AI model and provides specific
 * instructions for each use case.
 *
 * @property datastoreRepository The repository for accessing app-specific data, such as API keys and model names.
 */
class SystemAIModel @Inject constructor(datastoreRepository: DatastoreRepository) :
    BaseAIModel(datastoreRepository) {

    private val baseModel by lazy { modelBuilder.setApiKey(apiKey).setModel(modelName) }

    // Can be used as system instruction for generating chat title too [instruction name] does the trick.
    private val updateChatTitleInstruction: String =
        """
        [UPDATE CHAT TITLE]: 
        User will provide a prompt and system will only respond with the appropriate chat title for that prompt.
        Make sure the title is not too long, always prefer short title without loosing meaning.
        You will always analyze the prompt and generate a chat title based on the prompt.
        # NOTE: 
        - Do not reply with any other text.
        - Only provide the chat title as a response.
        - Do not hallucinate.
        - Do not generate any text that is not related to chat title.
        - Never Break Response Rules/Notes
    """
            .trimIndent()

    private val generateInitialPromptsInstruction: String =
        """
        [GENERATE INITIAL PROMPTS]:

        The system will analyze the user's previous prompts or chats (THIS WILL BE PROVIDED WITH THE PROMPT) 
        and generate a JSON response with a list of concise, relevant initial prompts for a new conversation. 
        Each prompt should be creative, accurate, and follow the formatting rules.

        ### Guidelines:

        1. Provide a JSON array of objects, where each object represents a suggested prompt with the following keys:
           - `text` (String): The concise and actionable prompt text.
           - `icon` (String): The appropriate icon from the provided `PromptIcon` enum that best matches the prompt.

        2. Map icons correctly to prompts based on the enum provided:
           ${PromptIcon.entries.map { " - ${it.description}: ${it.name} \n" }}

        3. Provide 4-6 diverse and relevant prompts based on the user's historical interests or queries.

        4. The JSON response should strictly follow this format:
        [
          {
            "text": "Prompt Text Here",
            "icon": "IconNameHere"
          },
          {
            "text": "Prompt Text Here",
            "icon": "IconNameHere"
          }
        ]

        ### Notes:
        - THE RESPONSE SHOULD BE IN PLAIN FORMAT, DO NOT USE CODE BLOCK ANY OTHER FORMATTING.
        - Avoid generic prompts; tailor them to the user's past interactions.
        - Prompts should encourage engagement and build on the user's interests effectively.
        - Never break response rules or include additional text or Markdown in the response.
        - Ensure the list is actionable and aligned with the enum class provided.

        ### Example Response:
        [
          {
            "text": "Write a Poem",
            "icon": "LITERATURE"
          },
          {
            "text": "How do different sorting algorithms compare in Python?",
            "icon": "CODE"
          },
          {
            "text": "Suggest some ways to optimize sorting in Python for large datasets",
            "icon": "CODE"
          }
        ]
    """
            .trimIndent()

    override fun getGenerativeModel(): GenerativeModel {
        return baseModel.build()
    }

    suspend fun generateChatTitle(prompt: String): String? {
        return getGenerativeModel()
            .generateContent(
                """
                System Instruction :$updateChatTitleInstruction,
                Prompt: $prompt
            """
                    .trimIndent()
            )
            .text
    }

    suspend fun generateInitialPrompts(prompt: String): List<PromptDto> {
        val model =
            baseModel
                .setConfig {
                    generationConfig {
                        temperature = 0.7f
                        topP = 0.75f
                        topK = 40
                        stopSequences = listOf("End of response", "STOP")
                        responseMimeType = "application/json"
                        //This does not seems to to have any effect as of now,
                        // need to check further for more clarity
                        responseSchema = PromptDto.SCHEMA.prompts
                    }
                }
                .build()

        return try {
            model
                .generateContent(
                    """
                System Instruction : $generateInitialPromptsInstruction,
                Prompt(Users Previous Chat History/Prompt): $prompt
            """
                        .trimIndent()
                )
                .text
                ?.let {
                    Log.d("SystemGemAIModel", "generateInitialPrompts: $it")
                    Json.decodeFromString<List<PromptDto>>(it)
                } ?: emptyList()
        } catch (e: Exception) {
            Log.e("SystemGemAIModel", "generateInitialPrompts: ${e.message}")
            emptyList()
        }
    }
}

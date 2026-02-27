package io.fletchly.comparator.port.out

import io.fletchly.comparator.model.message.Conversation
import io.fletchly.comparator.model.message.Message
import io.fletchly.comparator.model.message.MessageResult

/**
 * An interface representing the core functionality for generating responses in a conversational system.
 */
interface AIPort {
    /**
     * Generates a response from the AI assistant based on the provided system prompt and current conversation state.
     *
     * @param systemPrompt The system-level instruction or context used as input for generating the response.
     * @param conversation The current state of the conversation, containing the history of messages exchanged.
     * @return A [MessageResult] that indicates either a successful assistant response or an error.
     */
    suspend fun generateResponse(systemPrompt: String, conversation: Conversation): MessageResult<Message.Assistant>
}
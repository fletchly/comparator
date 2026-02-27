package io.fletchly.comparator.port.out

import io.fletchly.comparator.model.message.Conversation
import io.fletchly.comparator.model.message.Message
import io.fletchly.comparator.model.user.User

/**
 * Defines a port for managing user-specific conversational contexts.
 */
interface ContextPort {
    /**
     * Retrieves the conversational context associated with a specific user.
     *
     * @param user The user whose conversation context is to be retrieved.
     * @return The conversation associated with the specified user.
     */
    suspend fun get(user: User): Conversation

    /**
     * Appends a message to the conversational context associated with a user.
     *
     * @param user The user whose conversation context is being updated.
     * @param message The message to be appended to the user's conversation context.
     */
    suspend fun append(user: User, message: Message)

    /**
     * Clears the conversational context associated with the specified user.
     *
     * @param user The user whose conversational context is to be cleared.
     */
    suspend fun clear(user: User)

    /**
     * Clears all conversational contexts managed by the implementation.
     */
    suspend fun clearAll()
}
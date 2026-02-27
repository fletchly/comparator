package io.fletchly.comparator.core.port.`in`

import io.fletchly.comparator.core.model.user.User

/**
 * Provides an interface for clearing conversation context for one or more actors
 * in a conversational system.
 */
interface ClearContext {
    /**
     * Clears the conversational context for the specified target users.
     *
     * @param target A vararg parameter representing the users whose context
     *               should be cleared.
     */
    suspend fun clear(vararg target: User)

    /**
     * Clears the conversational context for the specified target users and notifies the sender.
     *
     * @param sender The user initiating the clear operation, typically for notification or logging purposes.
     * @param target A vararg parameter representing the users whose context should be cleared.
     */
    suspend fun clearWithFeedback(sender: User, vararg target: User)

    /**
     * Clears all conversational contexts within the system.
     */
    suspend fun clearAll()
}
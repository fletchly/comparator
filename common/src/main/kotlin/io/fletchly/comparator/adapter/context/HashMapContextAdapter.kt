package io.fletchly.comparator.adapter.context

import io.fletchly.comparator.model.message.Conversation
import io.fletchly.comparator.model.message.Message
import io.fletchly.comparator.model.user.User
import io.fletchly.comparator.port.out.ContextPort
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.util.UUID

/**
 * A thread-safe implementation of the `ContextPort` interface using a `HashMap` to store user-specific conversational contexts.
 *
 * This class provides functionality to manage and manipulate conversations for individual users, including retrieving,
 * appending messages, and clearing contexts, with each operation being synchronized to ensure thread safety.
 *
 * @constructor Initializes the adapter with a specified message limit for each conversation.
 * @param conversationMessageLimit The maximum number of messages allowed per conversation. When this limit is exceeded, the oldest messages are removed.
 */
class HashMapContextAdapter(private val conversationMessageLimit: Int) : ContextPort {
    private val mutex = Mutex()
    private val context = HashMap<UUID, Conversation>()

    /**
     * Retrieves the conversational context associated with the specified user.
     * If no existing conversation is found for the user, an empty conversation is returned.
     *
     * @param user The user whose conversation context is to be retrieved. The user is identified by a unique UUID.
     * @return The conversation associated with the specified user or an empty conversation if none exists.
     */
    override suspend fun get(user: User): Conversation = mutex.withLock {
        context[user.uniqueId] ?: Conversation.empty()
    }

    /**
     * Appends a message to the conversational context associated with the specified user.
     *
     * If no existing conversation is found for the user, a new empty conversation
     * is created to store the message. If the number of messages in the conversation
     * exceeds the allowed limit, the oldest message is removed.
     *
     * @param user The user whose conversation context is being updated.
     * @param message The message to add to the user's conversation context.
     */
    override suspend fun append(user: User, message: Message): Unit = mutex.withLock {
        val conversation = context.getOrPut(user.uniqueId) { Conversation.empty() }
        conversation.add(message)
        if (conversation.size() >= conversationMessageLimit) {
            conversation.removeOldest()
        }
    }

    /**
     * Clears the conversational context associated with the specified user.
     *
     * This method removes all stored messages and data related to the user's
     * conversation in the current context, effectively resetting the user's
     * conversation state to an empty state.
     *
     * @param user The user whose conversational context is to be cleared. The user
     *             is identified by a unique UUID.
     */
    override suspend fun clear(user: User): Unit = mutex.withLock {
        context.remove(user.uniqueId)
    }

    /**
     * Clears all conversational contexts managed by the current implementation.
     *
     * This method removes all stored data related to any user conversations within the system,
     * effectively resetting the entire context state to an empty state. It ensures thread-safe
     * execution by utilizing a mutex to prevent concurrent modifications to the context.
     */
    override suspend fun clearAll() = mutex.withLock {
        context.clear()
    }
}
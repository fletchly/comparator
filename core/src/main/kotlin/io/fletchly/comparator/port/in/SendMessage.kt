package io.fletchly.comparator.port.`in`

import io.fletchly.comparator.model.message.Message

/**
 * Represents a contract for sending user-generated messages within the system.
 *
 * This interface defines a method for handling messages originating from a user
 * and ensures appropriate processing of these messages within the system's
 * conversational context.
 */
interface SendMessage {
    /**
     * Handles a message sent by a user within the conversational system.
     *
     * This method is responsible for processing user-generated messages
     * and incorporating them into the conversation flow. The implementation
     * may include steps such as validating the message content, updating
     * relevant context, or triggering a system response.
     *
     * @param message The user-generated message to be processed. The message
     *                includes its content and the sender's information.
     */
    suspend fun fromUser(message: Message.User)
}
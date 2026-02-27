package io.fletchly.comparator.core.model.message

/**
 * Represents a series of messages exchanged in a conversational system.
 *
 * The `Conversation` class serves as an abstraction for managing the flow
 * of messages, including messages sent by users, responses generated
 * by an AI assistant, and outputs from tool executions.
 *
 * @property messages The collection of messages that form the conversation.
 */
@JvmInline
value class Conversation(private val messages: ArrayDeque<Message>) {
    fun size() = messages.size

    fun add(message: Message) {
        messages.add(message)
    }

    fun removeOldest() {
        messages.removeFirst()
    }


    companion object {
        fun empty() = Conversation(ArrayDeque())
    }
}
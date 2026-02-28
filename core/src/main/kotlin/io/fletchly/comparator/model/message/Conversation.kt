package io.fletchly.comparator.model.message

/**
 * Represents a series of messages exchanged in a conversational system.
 *
 * The `Conversation` class serves as an abstraction for managing the flow
 * of messages, including messages sent by users, responses generated
 * by an AI assistant, and outputs from tool executions.
 *
 * @property conversation The collection of messages that form the conversation.
 */
@JvmInline
value class Conversation(private val conversation: ArrayDeque<Message>) {
    val size get() = conversation.size
    val messages get() = conversation.toList()

    fun add(message: Message) {
        conversation.add(message)
    }

    fun removeOldest() {
        conversation.removeFirst()
    }

    companion object {
        fun empty() = Conversation(ArrayDeque())
    }
}
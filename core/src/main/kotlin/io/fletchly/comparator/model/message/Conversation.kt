package io.fletchly.comparator.model.message

/**
 * Represents a series of messages exchanged in a conversational system.
 *
 * The `Conversation` class serves as an abstraction for managing the flow
 * of messages, including messages sent by users, responses generated
 * by an AI assistant, and outputs from tool executions.
 *
 * @property messageQueue The collection of messages that form the conversation.
 */
@JvmInline
value class Conversation(private val messageQueue: ArrayDeque<Message>) {
    val size get() = messageQueue.size
    val messages get() = messageQueue.toList()

    fun add(message: Message) {
        messageQueue.add(message)
    }

    fun removeOldest() {
        messageQueue.removeFirst()
    }


    companion object {
        fun empty() = Conversation(ArrayDeque())
    }
}
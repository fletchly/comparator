package io.fletchly.comparator.model.message

/**
 * Represents the result of a message operation, which can either succeed or fail.
 *
 * Used to model the outcome of operations involving messages within the system.
 *
 * @param T The type of the message associated with a successful result, which must implement
 *          the `Message` interface.
 */
sealed interface MessageResult<out T : Message> {
    data class Success<T : Message>(val message: T) : MessageResult<T>
    data class Failure(val error: String) : MessageResult<Nothing>
}
package io.fletchly.comparator.core.model.message

sealed interface MessageResult<out T: Message> {
    data class Success<T: Message> (val message: T): MessageResult<T>
    data class Failure(val error: String): MessageResult<Nothing>
}
package io.fletchly.comparator.core.exception

/**
 * Represents an exception specific to tool definition issues.
 *
 * This exception is thrown when errors related to tool definitions are encountered,
 * such as invalid or non-serializable return types in the construction or validation
 * of tools.
 *
 * @constructor Creates a [ToolDefinitionException] with a specified error message and an optional cause.
 * @param message A detailed message describing the reason for the exception.
 * @param cause The underlying cause of the exception
 */
class ToolDefinitionException(message: String, cause: Throwable? = null) : Exception(message, cause)

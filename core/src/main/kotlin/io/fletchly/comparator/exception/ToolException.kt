package io.fletchly.comparator.exception

/**
 * Represents an exception that occurs during tool processing or execution.
 *
 * This exception is used to signify errors that happen when a tool is interacting
 * with its inputs, outputs, or defined behavior. It serves as a general exception
 * for tool-related runtime issues.
 *
 * @constructor Creates a [ToolException] with a specific error message.
 * @param message A detailed message describing the reason for the exception.
 */
class ToolException(override val message: String) : Exception(message)

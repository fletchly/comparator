package io.fletchly.comparator.model.message

import kotlinx.serialization.json.JsonObject

/**
 * Represents a tool call within the system, encapsulating the name of the tool
 * to be executed and its associated arguments.
 *
 * A `ToolCall` is used to trigger tool executions, where tools perform
 * specific operations or queries based on the provided arguments. Tool executions
 * can subsequently return results encapsulated as messages.
 *
 * @property name The name of the tool to be executed.
 * @property arguments The arguments to be passed to the tool, represented as
 *                     a JSON object.
 */
data class ToolCall(
    val name: String,
    val arguments: Map<String, Any>?
)
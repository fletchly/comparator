package io.fletchly.comparator.core.model.tool

import kotlinx.serialization.json.JsonElement

/**
 * Result of executing a [ToolDefinition]
 */
sealed interface ToolResult {
    val toolName: String

    data class Success(override val toolName: String, val value: JsonElement) : ToolResult
    data class Failure(override val toolName: String, val message: String) : ToolResult
}
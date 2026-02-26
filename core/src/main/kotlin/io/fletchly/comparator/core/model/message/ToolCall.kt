package io.fletchly.comparator.core.model.message

import kotlinx.serialization.json.JsonObject

/**
 * Call to execute a specific tool
 */
data class ToolCall(
    val name: String,
    val arguments: JsonObject
)
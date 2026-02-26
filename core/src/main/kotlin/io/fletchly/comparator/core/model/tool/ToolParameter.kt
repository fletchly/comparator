package io.fletchly.comparator.core.model.tool

import kotlin.reflect.KType

/**
 * Parameter in a [ToolDefinition] definition
 */
data class ToolParameter(
    val name: String,
    val type: Type,
    val description: String,
    val required: Boolean = true,
    val enum: List<String>? = null
) {
    enum class Type { STRING, INTEGER, NUMBER, BOOLEAN, OBJECT }
}

fun KType.toToolParameterType(): ToolParameter.Type = when (classifier) {
    String::class -> ToolParameter.Type.STRING
    Int::class, Long::class -> ToolParameter.Type.INTEGER
    Float::class, Double::class -> ToolParameter.Type.NUMBER
    Boolean::class -> ToolParameter.Type.BOOLEAN
    else -> ToolParameter.Type.OBJECT
}
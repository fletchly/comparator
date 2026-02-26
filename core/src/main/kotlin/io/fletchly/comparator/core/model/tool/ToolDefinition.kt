package io.fletchly.comparator.core.model.tool

/**
 * Structured definition of a tool
 */
interface ToolDefinition {
    val name: String
    val description: String
    val parameters: List<ToolParameter>
    suspend fun execute(args: Map<String, Any>): ToolResult
}

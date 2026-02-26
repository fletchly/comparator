package io.fletchly.comparator.core.model.tool

/**
 * Tool that can be used by an AI assistant
 */
class Tool(
    override val name: String,
    override val description: String,
    override val parameters: List<ToolParameter>,
    private val handler: suspend (Map<String, Any>) -> ToolResult
) : ToolDefinition {
    override suspend fun execute(args: Map<String, Any>): ToolResult = handler(args)
}
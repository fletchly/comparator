package io.fletchly.comparator.model.tool

/**
 * Represents an executable tool with a defined structure, parameters, and result handler.
 *
 * This class extends [ToolDefinition] and provides the implementation for
 * executing the tool using a suspendable handler function. It encapsulates the
 * tool's metadata, including its name, description, parameters, and execution logic.
 *
 * @property name The unique name of the tool.
 * @property description A brief description of what the tool does.
 * @property parameters A list of [ToolParameter] instances specifying the input parameters required by the tool.
 * @property handler A suspendable function that defines the execution logic of the tool.
 */
class Tool(
    override val name: String,
    override val description: String,
    override val parameters: List<ToolParameter>,
    private val handler: suspend (Map<String, Any>?) -> ToolResult
) : ToolDefinition {
    override suspend fun execute(args: Map<String, Any>?): ToolResult = handler(args)
}
package io.fletchly.comparator.model.tool

/**
 * Represents an executable tool with a defined structure, parameters, and result handler.
 *
 *
 * @property name The unique name of the tool.
 * @property description A brief description of what the tool does.
 * @property parameters A list of [ToolParameter] instances specifying the input parameters required by the tool.
 * @property handler A suspendable function that defines the execution logic of the tool.
 */
class Tool(
    val name: String,
    val description: String,
    val parameters: List<ToolParameter>,
    private val handler: suspend (Map<String, Any>) -> ToolResult
) {
    suspend fun execute(args: Map<String, Any>): ToolResult = handler(args)
}
package io.fletchly.comparator.core.model.tool

/**
 * Defines the structure of a tool, including its metadata, parameters, and execution behavior.
 *
 * This interface provides a contract for tools with a name, description, and a set of parameters
 * that define their input requirements. It also specifies the execution logic through a suspendable
 * function that takes input arguments and produces a result.
 *
 * @property name The unique name of the tool.
 * @property description A brief description of the tool's purpose or functionality.
 * @property parameters A list of parameters that define the required inputs for the tool. Each parameter
 * is represented as an instance of [ToolParameter], which includes details such as name, type, and description.
 */
interface ToolDefinition {
    val name: String
    val description: String
    val parameters: List<ToolParameter>
    suspend fun execute(args: Map<String, Any>): ToolResult
}

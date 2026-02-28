package io.fletchly.comparator.manager

import io.fletchly.comparator.model.message.Message
import io.fletchly.comparator.model.message.ToolCall
import io.fletchly.comparator.model.tool.Tool
import io.fletchly.comparator.model.tool.ToolResult
import io.fletchly.comparator.port.`in`.ToolRegistry

/**
 * Manages a collection of tools and facilitates their execution based on tool calls.
 *
 * The `ToolManager` class is responsible for handling a set of tools, ensuring that
 * each tool has a unique name within the collection. It provides functionality to
 * execute a tool based on a given `ToolCall` and return the result wrapped as a
 * `Message.Tool`. If the tool specified in the `ToolCall` is not found, an appropriate
 * message is returned.
 *
 * @constructor Initializes the `ToolManager` with a list of tools.
 *              Ensures that all tools have unique names during the initialization.
 * @param tools A list of `Tool` instances to be managed by the `ToolManager`.
 * @throws IllegalArgumentException if duplicate tool names are provided in the list.
 */
class ToolManager(tools: List<Tool>) : ToolRegistry {
    private val toolRegistry: Map<String, Tool>

    override val tools: List<Tool>
        get() = toolRegistry.values.toList()

    init {
        val duplicates = tools.groupBy { it.name }.filter { it.value.size > 1 }.keys
        require(duplicates.isEmpty()) {
            "Duplicate tool names: $duplicates"
        }
        this.toolRegistry = tools.associateBy { it.name }
    }

    suspend fun execute(toolCall: ToolCall): Message.Tool =
        when (val result = toolRegistry[toolCall.name]?.execute(toolCall.arguments)) {
            is ToolResult -> Message.Tool(result.toString())
            null -> Message.Tool("tool not found: ${toolCall.name}")
        }
}
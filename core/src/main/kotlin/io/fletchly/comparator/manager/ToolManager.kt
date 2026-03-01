/*
 * This file is part of comparator, licensed under the Apache License 2.0
 *
 * Copyright (c) 2026 fletchly <https://github.com/fletchly>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.fletchly.comparator.manager

import io.fletchly.comparator.model.message.Message
import io.fletchly.comparator.model.message.ToolCall
import io.fletchly.comparator.model.tool.Tool
import io.fletchly.comparator.util.ToolList
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
 * @param toolList A list of `Tool` instances to be managed by the `ToolManager`.
 * @throws IllegalArgumentException if duplicate tool names are provided in the list.
 */
class ToolManager(toolList: ToolList) : ToolRegistry {
    private val toolRegistry: Map<String, Tool>

    override val tools: List<Tool>
        get() = toolRegistry.values.toList()

    init {
        val duplicates = toolList.tools.groupBy { it.name }.filter { it.value.size > 1 }.keys
        require(duplicates.isEmpty()) {
            "Duplicate tool names: $duplicates"
        }
        this.toolRegistry = toolList.tools.associateBy { it.name }
    }

    suspend fun execute(toolCall: ToolCall): Message.Tool =
        when (val result = toolRegistry[toolCall.name]?.execute(toolCall.arguments)) {
            is ToolResult -> Message.Tool(result.toString())
            null -> Message.Tool("tool not found: ${toolCall.name}")
        }
}
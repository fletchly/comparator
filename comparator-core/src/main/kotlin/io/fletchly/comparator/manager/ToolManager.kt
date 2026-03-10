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
import io.fletchly.comparator.model.tool.ToolContext
import io.fletchly.comparator.model.tool.ToolResult
import io.fletchly.comparator.port.`in`.ToolExecutor
import io.fletchly.comparator.port.`in`.ToolRegistryLifecycle
import io.fletchly.comparator.port.out.LogPort
import io.fletchly.comparator.tool.ToolRegistry
import io.fletchly.comparator.util.pluralize

/**
 * Manages the registration and execution of tools within a system.
 *
 * @param log The logging interface used for recording informational and warning messages.
 */
class ToolManager(
    val log: LogPort
) : ToolExecutor, ToolRegistryLifecycle, ToolRegistry {
    private var tools: Map<String, Tool> = mutableMapOf()
    private var frozen = false

    override fun getTool(name: String): Tool? = tools[name]

    override fun getTools(): List<Tool> = tools.values.toList()

    override suspend fun execute(toolCall: ToolCall, toolContext: ToolContext): Message.Tool =
        when (val result = tools[toolCall.name]?.execute(toolCall.arguments, toolContext)) {
            is ToolResult.Success -> {
                val resultJson = result.toJsonString()
                val argumentsString = toolCall.arguments.map { "${it.key}: ${it.value}" }.joinToString(", ")
                val resultLogMessage = if (resultJson.length > TRUNCATE_LOGS) {
                    "${resultJson.take(TRUNCATE_LOGS)}... (${resultJson.length - TRUNCATE_LOGS} more characters)"
                } else {
                    resultJson
                }

                log.info("($argumentsString) : $resultLogMessage", result.toolName)
                Message.Tool(resultJson, result.toolName)
            }

            is ToolResult.Failure -> {
                val errorMessage = "encountered an error: ${result.error.message}"
                log.warn(errorMessage, result.toolName)
                Message.Tool(errorMessage, result.toolName)
            }

            null -> Message.Tool("tool not found", toolCall.name)
        }

    override fun register(vararg tools: Tool) {
        if (frozen) {
            log.warn("Tool registry has been frozen, skipping registration of the following ${"tool".pluralize(tools.size)}: ${tools.map { it.name }}")
        }
        tools.forEach { tool ->
            if (this.tools.contains(tool.name)) {
                log.warn(
                    "A tool with name '${tool.name}' is already registered, skipping", ToolManager::class.simpleName
                )
                return@forEach
            }
            (this.tools as MutableMap)[tool.name] = tool // safe to do here when the registry is unfrozen
        }
    }

    override fun freeze() {
        tools = tools.toMap()
        frozen = true
    }

    private companion object {
        const val TRUNCATE_LOGS = 100
    }
}
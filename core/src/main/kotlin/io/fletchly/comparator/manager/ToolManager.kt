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
import io.fletchly.comparator.model.tool.ToolResult
import io.fletchly.comparator.port.`in`.ToolExecutor
import io.fletchly.comparator.port.out.LogPort
import io.fletchly.comparator.tool.ToolRegistry

/**
 * Manages the registration and execution of tools within a system.
 *
 * @param log The logging interface used for recording informational and warning messages.
 */
class ToolManager(
    val log: LogPort
) : ToolExecutor, ToolRegistry {
    private val tools: MutableMap<String, Tool> = mutableMapOf()

    override fun getTools(): List<Tool> = tools.values.toList()

    override suspend fun execute(toolCall: ToolCall): Message.Tool =
        when (val result = tools[toolCall.name]?.execute(toolCall.arguments)) {
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
        tools.forEach { tool ->
            if (this.tools.contains(tool.name)) {
                log.warn(
                    "A tool with name '${tool.name}' is already registered, skipping", ToolManager::class.simpleName
                )
                return@forEach
            }
            this.tools[tool.name] = tool
        }
    }

    private companion object {
        const val TRUNCATE_LOGS = 100
    }
}
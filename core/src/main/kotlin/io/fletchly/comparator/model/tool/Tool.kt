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
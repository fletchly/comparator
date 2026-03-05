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

import io.fletchly.comparator.exception.ToolException

/**
 * Represents an executable tool with a defined structure, parameters, and result handler.
 *
 *
 * @property name The unique name of the tool.
 * @property description A brief description of what the tool does.
 * @property parameters A list of [Parameter] instances specifying the input parameters required by the tool.
 * @property handler A suspendable function that defines the execution logic of the tool.
 */
class Tool(
    val name: String,
    val description: String,
    val parameters: List<Parameter>,
    private val handler: suspend (Map<String, Any>) -> ToolResult
) {
    /**
     * Executes the tool using the provided arguments and validates input parameters.
     */
    suspend fun execute(args: Map<String, Any>): ToolResult {
        parameters.forEach { param ->
            if (param.required && !args.containsKey(param.name)) {
                return ToolResult.Failure(name, ToolException("Missing required parameter '${param.name}'", IllegalArgumentException()))
            }
            args[param.name]?.let { value ->
                if (!value.isCompatibleWith(param.type)) {
                    return ToolResult.Failure(name, ToolException("Parameter '${param.name}' expected ${param.type} but got ${value::class.simpleName}", IllegalArgumentException()))
                }
                if (param.type == Parameter.Type.ARRAY) {
                    val elementType = param.elementType ?: return@let
                    (value as List<*>).forEachIndexed { index, element ->
                        if (element == null || !element.isCompatibleWith(elementType)) {
                            return ToolResult.Failure(name, ToolException("Parameter '${param.name}[$index]' expected $elementType but got ${element?.let { it::class.simpleName } ?: "null"}", IllegalArgumentException()))
                        }
                    }
                }
            }
        }
        return handler(args)
    }

    private fun Any.isCompatibleWith(type: Parameter.Type): Boolean = when (type) {
        Parameter.Type.STRING -> this is String
        Parameter.Type.INTEGER -> this is Int || this is Long
        Parameter.Type.NUMBER -> this is Float || this is Double
        Parameter.Type.BOOLEAN -> this is Boolean
        Parameter.Type.ARRAY -> this is List<*>
    }
}
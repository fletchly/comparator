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

package io.fletchly.comparator.adapter.ollama.dto

import io.fletchly.comparator.util.JsonSchema
import kotlinx.serialization.Serializable

/**
 * ChatTool definition
 *
 * @see <a href="https://docs.ollama.com/api/chat">Ollama Chat API docs</a>
 *
 * @property type type of tool (always `function`)
 * @property function tool function definition
 */
@Serializable
data class ChatTool(val function: ChatToolFunction) {
    @Suppress("unused")
    val type: String = "function"
}

/**
 * ChatTool function definition
 *
 * @see <a href="https://docs.ollama.com/api/chat">Ollama Chat API docs</a>
 *
 * @property name name of the function to call
 * @property description what the function does
 * @property parameters JSON Schema for the function parameters
 */
@Serializable
data class ChatToolFunction(
    val name: String,
    val parameters: JsonSchema,
    val description: String? = null
)
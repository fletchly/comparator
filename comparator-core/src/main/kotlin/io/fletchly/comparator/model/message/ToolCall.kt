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

package io.fletchly.comparator.model.message

/**
 * Represents a tool call within the system, encapsulating the name of the tool
 * to be executed and its associated arguments.
 *
 * A `ToolCall` is used to trigger tool executions, where tools perform
 * specific operations or queries based on the provided arguments. Tool executions
 * can subsequently return results encapsulated as messages.
 *
 * @property name The name of the tool to be executed.
 * @property arguments The arguments to be passed to the tool, represented as
 *                     a JSON object.
 */
data class ToolCall(
    val name: String,
    val arguments: Map<String, Any>
)
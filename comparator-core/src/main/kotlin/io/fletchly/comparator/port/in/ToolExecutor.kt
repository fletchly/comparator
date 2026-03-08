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

package io.fletchly.comparator.port.`in`

import io.fletchly.comparator.model.message.Message
import io.fletchly.comparator.model.message.ToolCall
import io.fletchly.comparator.model.tool.ToolContext

/**
 * Defines a registry for managing a collection of tools within the system.
 *
 * This interface provides access to a list of tools that can be executed within
 * the system. The tools encapsulate specific functionality and are designed to
 * handle various tasks as part of the system's operations.
 *
 * @property tools A list of tools available in the registry. Each tool contains
 *                 its definition and execution logic.
 */
interface ToolExecutor {
    suspend fun execute(toolCall: ToolCall, toolContext: ToolContext): Message.Tool
}
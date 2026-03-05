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

package io.fletchly.comparator.tool

import io.fletchly.comparator.model.tool.Tool

/**
 * Defines a registry for managing tools that can be executed.
 *
 * An implementation of this interface is responsible for maintaining a collection of tools and providing
 * functionality to register new tools. Tools are expected to conform to the [Tool] structure, which includes
 * metadata such as name, description, input parameters, and execution logic.
 */
interface ToolRegistry {
    fun register(vararg tools: Tool)
    fun getTools(): List<Tool>
}
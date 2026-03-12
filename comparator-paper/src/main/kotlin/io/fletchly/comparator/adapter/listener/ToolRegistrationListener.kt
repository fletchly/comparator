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

package io.fletchly.comparator.adapter.listener

import io.fletchly.comparator.event.ToolRegistrationEvent
import io.fletchly.comparator.model.options.BuiltInToolOptions
import io.fletchly.comparator.tool.currentDateTool
import io.fletchly.comparator.tool.gameVersionTool
import io.fletchly.comparator.tool.playerInfoTool
import io.fletchly.comparator.tool.webSearchTool
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener

/**
 * Handles the registration of built-in tools during the `ToolRegistrationEvent`.
 *
 * This listener registers pre-defined tools (such as web search, game version,
 * and current date tools) based on the provided configuration options. The tools
 * are conditionally added to the `ToolRegistry` based on the state of the
 * `BuiltInToolOptions` instance.
 *
 * @param options Configuration settings that control the inclusion of various
 * built-in tools in the tool registration process.
 */
class ToolRegistrationListener(
    private val options: BuiltInToolOptions
) : Listener {
    @EventHandler
    fun onToolRegistration(event: ToolRegistrationEvent) {
        val registry = event.registry

        if (options.webSearchEnabled) registry.register(webSearchTool)
        if (options.gameVersionEnabled) registry.register(gameVersionTool)
        if (options.currentDateEnabled) registry.register(currentDateTool)
        if (options.playerInfoEnabled) registry.register(playerInfoTool)
    }
}
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

package io.fletchly.comparator.adapter.tool

import io.fletchly.comparator.infra.BukkitPluginRuntime
import io.fletchly.comparator.model.tool.ToolDefinition
import io.fletchly.comparator.model.tool.tool
import org.bukkit.plugin.java.JavaPlugin
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module
import java.time.LocalDate

/**
 * A tool that retrieves the current game server version and the current date.
 *
 * @property plugin The JavaPlugin instance associated with this tool.
 * @property pluginRuntime A scheduler utility for running tasks and coroutines safely within the plugin context.
 */
class GameVersionTool(
    private val plugin: JavaPlugin,
    private val pluginRuntime: BukkitPluginRuntime
): ToolDefinition {
    suspend fun getGameVersion() = pluginRuntime.runTask {
        mapOf("version" to plugin.server.version, "date" to LocalDate.now().toString())
    }

    override val definition = tool("game_version") {
        description = "Get the version of the current server and the current date. Used to ensure up-to-date information."
        executes(this@GameVersionTool::getGameVersion)
    }

    companion object {
        val module = module {
            singleOf(::GameVersionTool) bind ToolDefinition::class
        }
    }
}
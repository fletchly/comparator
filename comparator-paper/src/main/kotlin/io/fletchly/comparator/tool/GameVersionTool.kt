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

import io.fletchly.comparator.annotation.ToolFunction
import io.fletchly.comparator.infra.BukkitPluginRuntime
import org.bukkit.plugin.java.JavaPlugin
import org.koin.java.KoinJavaComponent.getKoin

class GameVersionTool(
    private val plugin: JavaPlugin,
    private val pluginRuntime: BukkitPluginRuntime
) {
    @ToolFunction(name = "game_version", description = "Get the Minecraft version the player is currently running on their client/server **THIS MAY OR MAY NOT BE THE MOST RECENT GAME VERSION**. Useful for ensuring version-relevant information.")
    suspend fun getGameVersion(): Map<String, String> = pluginRuntime.runTask {
        mapOf("version" to plugin.server.version)
    }
}

private val instance: GameVersionTool = getKoin().get()
val gameVersionTool = tool(instance::getGameVersion)
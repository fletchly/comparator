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

package io.fletchly.comparator

import io.fletchly.comparator.adapter.config.PluginConfigService
import io.fletchly.comparator.infra.BukkitPluginRuntime
import io.fletchly.comparator.infra.KoinBootstrapper
import io.fletchly.comparator.model.command.CommandDefinition
import io.fletchly.comparator.model.command.registerCommand
import io.fletchly.comparator.model.event.ToolRegistrationEvent
import io.fletchly.comparator.model.tool.Tool
import io.fletchly.comparator.port.`in`.ContextClearer
import io.fletchly.comparator.tool.ToolRegistry
import io.fletchly.comparator.tool.gameInfoTool
import io.fletchly.comparator.tool.webSearchTool
import io.fletchly.comparator.util.pluralize
import io.fletchly.comparator.util.registerEventListener
import kotlinx.coroutines.runBlocking
import org.bukkit.event.Listener
import org.bukkit.plugin.java.JavaPlugin
import org.koin.core.context.stopKoin

class Comparator : JavaPlugin() {
    private var koinBootstrapper = KoinBootstrapper(this)
    private val koin = koinBootstrapper.start()

    override fun onEnable() {
        registerCommands()
        registerEventListeners()
        registerTools()

        logger.info { "Successfully enabled Comparator ${pluginMeta.version}. Happy chatting! \uD83D\uDCA1" }
    }

    override fun onDisable() {
        val context = koin.get<ContextClearer>()
        val scheduler = koin.get<BukkitPluginRuntime>()

        logger.info { "Clearing context for all users" }
        runBlocking { context.clearAll() }

        logger.info { "Shutting down plugin scheduler" }
        scheduler.cancel()

        stopKoin()
    }

    private fun registerCommands() {
        val commands = koin.getAll<CommandDefinition>()
        var registered = 0

        commands.forEach {
            registerCommand(it.definition)
            registered++
        }

        logger.info { "Registered $registered ${"command".pluralize(registered)}" }
    }

    private fun registerEventListeners() {
        val eventListeners = koin.getAll<Listener>()
        var registered = 0

        eventListeners.forEach {
            registerEventListener(it)
            registered++
        }

        logger.info { "Registered $registered event ${"listener".pluralize(registered)}" }
    }

    private fun registerTools() {
        val registry = koin.get<ToolRegistry>()
        val config = koin.get<PluginConfigService>().config.tool

        val registeredNames = mutableListOf<String>()

        fun registerBuiltIn(tool: Tool) {
            registry.register(tool)
            registeredNames.add("${tool.name} (built-in)")
        }

        if (config.webSearch.enabled) registerBuiltIn(webSearchTool)
        if (config.gameVersion.enabled) registerBuiltIn(gameInfoTool)

        server.pluginManager.callEvent(ToolRegistrationEvent(registry))

        registeredNames.addAll(registry.getTools().map { it.name })

        logger.info { "Registered ${registeredNames.size} ${"tool".pluralize(registeredNames.size)}: $registeredNames" }
    }
}
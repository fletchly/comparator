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

import io.fletchly.comparator.model.command.CommandDefinition
import io.fletchly.comparator.model.command.registerCommand
import io.fletchly.comparator.infra.BukkitPluginRuntime
import io.fletchly.comparator.infra.KoinBootstrapper
import io.fletchly.comparator.model.tool.ToolDefinition
import io.fletchly.comparator.model.tool.ToolList
import io.fletchly.comparator.port.`in`.ContextClearer
import io.fletchly.comparator.port.`in`.ToolRegistry
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
        koinBootstrapper.loadToolModules(koin)
        val toolRegistry = koin.get<ToolRegistry>()
        val tools = koin.getAll<ToolDefinition>()

        toolRegistry.register(ToolList(tools.map { it.definition }))

        logger.info { "Registered ${tools.size} ${"tool".pluralize(tools.size)} [${tools.joinToString(", ") { it.definition.name }}]" }
    }
}
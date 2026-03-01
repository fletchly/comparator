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

import io.fletchly.comparator.adapter.command.model.CommandDefinition
import io.fletchly.comparator.adapter.command.model.registerCommand
import io.fletchly.comparator.adapter.config.PluginConfigService
import io.fletchly.comparator.adapter.config.SystemPromptService
import io.fletchly.comparator.di.commonAdapterModule
import io.fletchly.comparator.di.coreModule
import io.fletchly.comparator.di.paperAdapterModule
import io.fletchly.comparator.di.paperConfigModule
import io.fletchly.comparator.di.paperInfraModule
import io.fletchly.comparator.infra.scheduler.PluginScheduler
import io.fletchly.comparator.model.tool.ToolDefinition
import io.fletchly.comparator.port.`in`.ContextClearer
import kotlinx.coroutines.runBlocking
import org.bukkit.event.Listener
import org.bukkit.plugin.java.JavaPlugin
import org.koin.core.Koin
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.core.module.Module
import org.koin.java.KoinJavaComponent.getKoin

class Comparator : JavaPlugin() {
    private lateinit var koin: Koin

    override fun onEnable() {
        loadConfig()
        initializeModules()
        registerCommands()
        // registerEventListeners()
        // registerTools()

        logger.info { "Successfully enabled Comparator ${pluginMeta.version}. Happy chatting! \uD83D\uDCA1" }
    }

    override fun onDisable() {
        val context = koin.get<ContextClearer>()
        val scheduler = koin.get<PluginScheduler>()

        logger.info { "Clearing context for all users" }
        runBlocking { context.clearAll() }

        logger.info { "Shutting down plugin scheduler" }
        scheduler.cancel()

        stopKoin()
    }

    private fun loadConfig() {
        koin.get<PluginConfigService>().saveDefault()
        koin.get<SystemPromptService>().saveDefault()
        koin.get<PluginConfigService>().loadConfig()
        koin.get<SystemPromptService>().loadPrompt()
    }

    private fun initializeModules() {
        startKoin {
            modules(
                coreModule,
                paperConfigModule,
                commonAdapterModule,
                paperInfraModule(this@Comparator),
                paperAdapterModule
            )
        }

        koin = getKoin()
    }

    private fun registerCommands() {
        val commands = koin.getAll<CommandDefinition>()
        var registered = 0

        commands.forEach {
            registerCommand(it.definition)
            registered++
        }

        logger.info { "Registered $registered commands" }
    }

    private fun registerEventListeners() {
        val eventListeners = koin.getAll<Listener>()
        var registered = 0

        eventListeners.forEach { _ ->
            registered++
            TODO("extension function")
        }

        logger.info { "Registered $registered event listeners" }
    }

//    private fun registerTools() {
//        val tools = koin.getAll<ToolDefinition>()
//
//        TODO("Not implemented yet")
//    }
}
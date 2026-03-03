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

package io.fletchly.comparator.di

import io.fletchly.comparator.adapter.chat.PaperChatService
import io.fletchly.comparator.adapter.chat.PaperNotificationService
import io.fletchly.comparator.adapter.command.AdminCommand
import io.fletchly.comparator.adapter.command.AskCommand
import io.fletchly.comparator.model.command.CommandDefinition
import io.fletchly.comparator.adapter.event.BukkitPlayerEvents
import io.fletchly.comparator.adapter.event.PaperChatEvents
import io.fletchly.comparator.adapter.lifecycle.BukkitPluginLifecycleScope
import io.fletchly.comparator.adapter.logger.BukkitPluginLogger
import io.fletchly.comparator.port.out.ChatPort
import io.fletchly.comparator.port.out.LogPort
import io.fletchly.comparator.port.out.NotificationPort
import io.fletchly.comparator.port.out.ScopePort
import org.bukkit.event.Listener
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

private val commands = module {
    singleOf(::AdminCommand) bind CommandDefinition::class
    singleOf(::AskCommand) bind CommandDefinition::class
}

private val events = module {
    singleOf(::BukkitPlayerEvents) bind Listener::class
    singleOf(::PaperChatEvents) bind Listener::class
}

val paperAdapterModule = module {
    includes(commands, events)
    singleOf(::PaperNotificationService) bind NotificationPort::class
    singleOf(::PaperChatService) bind ChatPort::class
    singleOf(::BukkitPluginLogger) bind LogPort::class
    singleOf(::BukkitPluginLifecycleScope) bind ScopePort::class
}
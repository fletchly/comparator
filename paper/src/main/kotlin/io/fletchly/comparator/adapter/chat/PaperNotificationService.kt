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

package io.fletchly.comparator.adapter.chat

import io.fletchly.comparator.infra.scheduler.PluginScheduler
import io.fletchly.comparator.model.BukkitPlayerUser
import io.fletchly.comparator.model.ConsoleUser
import io.fletchly.comparator.model.user.User
import io.fletchly.comparator.port.out.NotificationPort
import io.papermc.paper.registry.keys.SoundEventKeys
import net.kyori.adventure.sound.Sound
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.Component.text
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.plugin.java.JavaPlugin

class PaperNotificationService(
    private val pluginScheduler: PluginScheduler,
    plugin: JavaPlugin
) : NotificationPort {
    private val server = plugin.server

    override suspend fun info(user: User, message: String) =
        pluginScheduler.runTask { user.sendMessage(infoMessage(message)) }

    override suspend fun error(user: User, message: String) = pluginScheduler.runTask {
        if (user is BukkitPlayerUser) user.player.playSound(ERROR_SOUND)
        user.sendMessage(errorMessage(message))
    }

    private fun User.sendMessage(message: Component) {
        when (this) {
            is BukkitPlayerUser -> this.player.sendMessage { message }
            is ConsoleUser -> server.consoleSender.sendMessage { message }
        }
    }

    private fun infoMessage(message: String) = text(message).color(NamedTextColor.YELLOW)

    private fun errorMessage(message: String) = text(message).color(NamedTextColor.RED)

    private companion object {
        val ERROR_SOUND = Sound.sound(
            SoundEventKeys.BLOCK_GLASS_BREAK,
            Sound.Source.MASTER,
            1f,
            1f,
        )
    }
}
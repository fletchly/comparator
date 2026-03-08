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

import io.fletchly.comparator.infra.BukkitPluginRuntime
import io.fletchly.comparator.model.actor.Actor
import io.fletchly.comparator.model.actor.BukkitChatActor
import io.fletchly.comparator.model.actor.BukkitPlayerActor
import io.fletchly.comparator.model.actor.ConsoleActor
import io.fletchly.comparator.port.out.NotificationPort
import io.fletchly.comparator.util.fromMiniMessage
import io.papermc.paper.registry.keys.SoundEventKeys
import net.kyori.adventure.sound.Sound
import net.kyori.adventure.text.Component
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin

/**
 * A service implementation of the `NotificationPort` for sending user notifications
 * using the Bukkit API. This service facilitates player-specific notifications, such
 * as sending informational or error messages, with integration to the Bukkit server environment.
 *
 * @constructor Initializes the `PaperNotificationService` with a plugin scheduler and plugin instance.
 * @param pluginRuntime The scheduler used for managing tasks on the server's main thread.
 * @param plugin The plugin instance providing the server context and resources.
 */
class PaperNotificationService(
    private val pluginRuntime: BukkitPluginRuntime,
    plugin: JavaPlugin
) : NotificationPort {
    private val server = plugin.server

    override suspend fun info(target: Actor, message: String) =
        pluginRuntime.runTask { target.sendMessage(infoMessage(message)) }

    override suspend fun error(target: Actor, message: String) =
        pluginRuntime.runTask {
            target.sendMessage(errorMessage(message)) { player -> player.playSound(ERROR_SOUND) }
        }

    private fun Actor.sendMessage(message: Component, onPlayer: ((Player) -> Unit)? = null) {
        if (!this.isOnline) return
        when (this) {
            is BukkitPlayerActor -> {
                onPlayer?.invoke(this.player)
                this.player.sendMessage(message)
            }

            is BukkitChatActor -> {
                onPlayer?.invoke(this.player)
                server.broadcast(fromMiniMessage("<<green>$AGENT_NAME</green>> ").append(message))
            }

            is ConsoleActor -> server.consoleSender.sendMessage(message)
        }
    }

    private fun infoMessage(message: String) = fromMiniMessage("<yellow>$message</yellow>")

    private fun errorMessage(message: String) = fromMiniMessage("<red>$message</red>")

    private companion object {
        val ERROR_SOUND = Sound.sound(
            SoundEventKeys.BLOCK_GLASS_BREAK,
            Sound.Source.MASTER,
            1f,
            1f,
        )
    }
}
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
import io.fletchly.comparator.model.user.BukkitPlayerUser
import io.fletchly.comparator.model.user.ConsoleUser
import io.fletchly.comparator.model.message.Message
import io.fletchly.comparator.model.user.BukkitChatPlayerUser
import io.fletchly.comparator.model.user.User
import io.fletchly.comparator.port.out.ChatPort
import io.papermc.paper.registry.keys.SoundEventKeys
import net.kyori.adventure.sound.Sound
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.Component.text
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.plugin.java.JavaPlugin

/**
 * Handles the display of chat messages to users using a Bukkit-based server.
 *
 * This class bridges the `ChatPort` interface with a server-driven implementation,
 * allowing customized rendering of messages from users, assistants, or tools.
 * It uses the provided `BukkitPluginRuntime` to ensure tasks are executed on the appropriate
 * server thread and manages message formatting for different user types.
 *
 * @constructor Creates an instance of `PaperChatService`.
 * @param pluginRuntime A scheduler used to execute tasks on the server thread.
 * @param plugin The plugin instance associated with the chat service.
 */
class PaperChatService(
    private val pluginRuntime: BukkitPluginRuntime,
    plugin: JavaPlugin
) : ChatPort {
    private val server = plugin.server

    override suspend fun message(
        target: User,
        message: Message
    ) = pluginRuntime.runTask {
        val isPublic = target is BukkitChatPlayerUser

        when (message) {
            is Message.User -> if (!isPublic) target.sendMessage(userMessage(message))
            is Message.Assistant -> target.sendMessage(assistantMessage(message, isPublic), true)
            is Message.Tool -> error("Tool messages should not be directly displayed in chat")
        }
    }

    private fun User.sendMessage(message: Component, withSound: Boolean = false) {
        if (!this.isOnline) return
        when (this) {
            is BukkitPlayerUser -> {
                if (withSound) this.player.playSound(RESPONSE_SOUND)
                this.player.sendMessage(message)
            }

            is BukkitChatPlayerUser -> server.broadcast(message)

            is ConsoleUser -> server.consoleSender.sendMessage(message)
        }
    }

    private fun userMessage(message: Message.User) =
        PLAYER_PREFIX
            .append(text(message.sender.displayName))
            .append(ARROW)
            .append(text(message.content, NamedTextColor.GRAY))

    private fun assistantMessage(message: Message.Assistant, isPublic: Boolean = false) =
        when (isPublic) {
            true -> PUBLIC_AGENT_PREFIX
                .append(text(message.content))
            false -> PRIVATE_AGENT_PREFIX
                .append(ARROW, text(message.content, NamedTextColor.WHITE))
        }

    // TODO: Improve text rendering
    private companion object {
        val ARROW = text(" → ", NamedTextColor.WHITE)
        val AGENT_NAME = text("Comparator", NamedTextColor.GREEN)
        val PLAYER_PREFIX = text(" \uD83D\uDC64 ", NamedTextColor.AQUA) // 👤
        val PRIVATE_AGENT_PREFIX = text("\uD83D\uDCA1", NamedTextColor.GREEN).append(AGENT_NAME) // 💡
        val PUBLIC_AGENT_PREFIX = text("<").append(AGENT_NAME, text("> "))


        val RESPONSE_SOUND = Sound.sound(
            SoundEventKeys.ENTITY_EXPERIENCE_ORB_PICKUP,
            Sound.Source.MASTER,
            1f,
            1f,
        )
    }
}
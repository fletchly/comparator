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

package io.fletchly.comparator.adapter.event

import io.fletchly.comparator.infra.BukkitPluginRuntime
import io.fletchly.comparator.model.message.Message
import io.fletchly.comparator.model.options.PublicChatPrefixOptions
import io.fletchly.comparator.model.user.BukkitChatPlayerUser
import io.fletchly.comparator.port.`in`.MessageSender
import io.papermc.paper.event.player.AsyncChatEvent
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener

/**
 * Handles chat-related events within a Paper-based environment.
 *
 * @constructor Initializes the `PaperChatEvents` listener with dependencies for
 * handling messages, managing command prefixes, and executing asynchronous tasks.
 *
 * @param prefixOptions The bot prefix used to identify commands intended for the bot.
 *                  Commands must start with this prefix to be recognized.
 * @param messageSender The component responsible for handling user-generated
 * messages by passing them to the system's messaging pipeline.
 * @param pluginRuntime A utility for managing coroutine execution and task scheduling
 * specific to the lifecycle of a Bukkit plugin.
 */
class PaperChatEvents(
    prefixOptions: PublicChatPrefixOptions,
    private val messageSender: MessageSender,
    private val pluginRuntime: BukkitPluginRuntime
) : Listener {
    private val prefix = prefixOptions.prefix

    /**
     * Handles asynchronous chat events, processes user messages intended for the bot,
     * and forwards them for further processing. This method ensures that only messages
     * starting with the defined bot prefix are handled, extracts the relevant content
     * from those messages, and invokes the necessary systems to process them.
     *
     * @param event The `AsyncChatEvent` instance representing the chat message event.
     *              It contains details about the sender, message content, and other
     *              metadata related to the chat interaction.
     */
    @EventHandler
    fun onAsyncChat(event: AsyncChatEvent) {
        val plainText = PlainTextComponentSerializer.plainText().serialize(event.message())
        if (!plainText.startsWith("$prefix ")) return

        val prompt = plainText.substring(prefix.length).trim()
        val user = BukkitChatPlayerUser(event.player)
        val userMessage = Message.User(prompt, user)

        pluginRuntime.runCoroutine {
            messageSender.fromUser(userMessage)
        }

        event.message(assistantMentionComponent(prompt))
    }

    private fun assistantMentionComponent(prompt: String) = Component
        .text(prefix, NamedTextColor.AQUA)
        .append(Component.text(" $prompt", NamedTextColor.WHITE))
}
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

package io.fletchly.comparator.model.actor

import io.fletchly.comparator.model.message.ChatConversationKey
import io.fletchly.comparator.model.message.ConversationKey
import org.bukkit.entity.Player

/**
 * Represents a player interacting with the assistant via public chat.
 *
 * This value class implements the `Actor` interface, allowing a `Player`
 * in a Bukkit-based server environment to be treated as an actor within
 * the conversation system. This actor is associated with a predefined
 * conversation key and always considered online.
 *
 * @property player The underlying Bukkit `Player` for this chat actor.
 * @property displayName The name of the player as it appears in the chat.
 * @property conversationKey The conversation key associated with the public chat.
 * @property isOnline Indicates whether the player is online. Always `true` for public chat interactions.
 */
@JvmInline
value class BukkitChatActor(val player: Player) : Actor {
    override val displayName: String get() = player.name
    override val conversationKey: ConversationKey get() = ChatConversationKey
    override val isOnline: Boolean get() = true
}
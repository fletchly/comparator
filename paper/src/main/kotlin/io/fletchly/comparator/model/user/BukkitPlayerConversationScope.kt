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

package io.fletchly.comparator.model.user

import org.bukkit.entity.Player

/**
 * Represents a conversation scope tied to a specific Bukkit player.
 *
 * This class implements the [RestrictedConversationScope] interface, providing a player-specific context
 * for conversational interactions. The scope is defined by the associated [Player] instance, allowing access
 * to player-specific details such as display name, unique identifier, and online status.
 *
 * It is used in scenarios where interactions or notifications are directed at a specific player, ensuring
 * the scope and behavior are restricted to that player's context.
 *
 * @param player The Bukkit player associated with this conversation scope.
 */
@JvmInline
value class BukkitPlayerConversationScope(val player: Player) : RestrictedConversationScope {
    override val displayName get() = player.name
    override val uniqueId get() = player.uniqueId
    override val isOnline get() = player.isOnline
}
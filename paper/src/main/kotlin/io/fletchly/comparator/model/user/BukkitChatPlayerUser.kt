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
import java.util.UUID

/**
 * Represents a pseudo-user for public chat interactions within the system.
 *
 * This class wraps a `Player` instance and implements the `User` interface, providing an abstraction
 * for users who participate solely in public chat activities. The `uniqueId` is deliberately set
 * to a static UUID specific to chat interactions, distinguishing it from other user types.
 *
 * Unlike typical `User` implementations, the `isOnline` property always returns `true`,
 * signifying that the public chat user context is always considered active.
 *
 * @property player The underlying `Player` instance associated with the pseudo-user.
 */
@JvmInline
value class BukkitChatPlayerUser(val player: Player) : User {
    override val displayName get() =  player.name
    override val uniqueId get() = CHAT_UUID
    override val isOnline get() = true

    private companion object {
        val CHAT_UUID: UUID = UUID.nameUUIDFromBytes("CHAT".toByteArray())
    }
}
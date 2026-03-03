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
 * A lightweight wrapper around the `Player` class that implements the `User` interface.
 *
 * This class allows a `Player` instance to seamlessly integrate with systems expecting a `User`.
 * It provides access to the player's display name, unique identifier, and operator status.
 *
 * @property player The underlying `Player` instance being wrapped.
 */
@JvmInline
value class BukkitPlayerUser(val player: Player): User {
    override val displayName get() = player.name
    override val uniqueId get() = player.uniqueId
    override val isOnline get() = player.isOnline
}
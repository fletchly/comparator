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
import io.fletchly.comparator.model.user.BukkitPlayerUser
import io.fletchly.comparator.port.`in`.ContextClearer
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerQuitEvent

/**
 * Handles player-related Bukkit events and performs asynchronous operations
 * based on the player's interaction with the server.
 *
 * This class listens for Bukkit `PlayerQuitEvent` and integrates with
 * conversational systems or other context-sensitive features by utilizing
 * `ContextClearer` and `BukkitPluginRuntime` components.
 *
 * @param context A `ContextClearer` instance used for clearing the player's conversational context.
 * @param pluginRuntime A `BukkitPluginRuntime` instance for managing asynchronous tasks within the plugin's lifecycle.
 */
class BukkitPlayerEvents(
    private val context: ContextClearer,
    private val pluginRuntime: BukkitPluginRuntime
): Listener {
    @EventHandler
    fun onPlayerQuit(event: PlayerQuitEvent) {
        val target = BukkitPlayerUser(event.player)

        pluginRuntime.runCoroutine {
            with (context) {
                target.clearSelf()
            }
        }
    }
}
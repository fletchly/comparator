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

package io.fletchly.comparator.tool

import io.fletchly.comparator.annotation.ToolFunction
import io.fletchly.comparator.infra.BukkitPluginRuntime
import io.fletchly.comparator.model.options.PlayerInfoOptions
import io.fletchly.comparator.model.tool.ToolContext
import io.fletchly.comparator.model.tool.bukkitPlayer
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.PlayerInventory
import org.koin.java.KoinJavaComponent.getKoin

/**
 * A utility class designed to retrieve information about a player's current in-game context.
 *
 * The class provides capabilities to gather details such as the player's surroundings, inventory,
 * nearby entities, biome, game mode, and the object or entity they are currently looking at.
 *
 * @constructor Creates an instance of PlayerInfoTool.
 * @param pluginRuntime The runtime environment for managing Bukkit plugin tasks and coroutines.
 * @param playerInfoOptions Configuration options defining how player information, such as entity search radius
 * and looking distance, is retrieved.
 */
class PlayerInfoTool(
    private val pluginRuntime: BukkitPluginRuntime,
    private val playerInfoOptions: PlayerInfoOptions
) {
    /**
     * Retrieves the player's current in-game context, including details about their surroundings,
     * nearby entities, inventory, and the block or entity they are currently looking at.
     *
     * @param toolContext The context containing information about the current player and related data.
     * @return A map containing key-value pairs with information about the player:
     * - "time": The current in-game time of the player's world.
     * - "environment": The environment type of the player's world (e.g., normal, nether, end).
     * - "biome": The biome where the player is currently located.
     * - "inventory": A summarized list of items in the player's inventory.
     * - "nearby_entities": A summarized list of entities near the player.
     * - "looking_at": The block or entity the player is currently looking at.
     * - "game_mode": The player's current game mode (e.g., survival, creative).
     */
    @ToolFunction(
        name = "player_info",
        description = "retrieves the player's current in-game context, including their surroundings, nearby entities, inventory, and the block/entity they are currently looking at."
    )
    suspend fun getPlayerInfo(toolContext: ToolContext): Map<String, String> = pluginRuntime.runTask {
        val player = toolContext.bukkitPlayer
            ?: return@runTask mapOf("info" to "tool executed from console, no meaningful information to provide")

        val time: Long = player.world.time
        val environment: String = player.world.environment.name
        val biome: String = player.location.block.biome.key().asString()
        val inventory: String = summarizeInventory(player.inventory)
        val nearbyEntities: String = summarizeNearbyEntities(
            player,
            playerInfoOptions.entityRadiusX,
            playerInfoOptions.entityRadiusY,
            playerInfoOptions.entityRadiusZ
        )
        val lookingAt: String = getLookingAt(player, playerInfoOptions.lookingAtDistance)
        val gameMode: String = player.gameMode.name

        mapOf(
            "time" to time.toString(),
            "environment" to environment,
            "biome" to biome,
            "inventory" to inventory,
            "nearby_entities" to nearbyEntities,
            "looking_at" to lookingAt,
            "game_mode" to gameMode
        )
    }

    private fun summarizeInventory(inventory: PlayerInventory): String =
        inventory.contents.filterNotNull().filter { it.type != Material.AIR }.joinToString(
            separator = ", ", prefix = "[", postfix = "]"
        ) { "${it.amount}x${it.type.name}" }

    private fun summarizeNearbyEntities(player: Player, dx: Double, dy: Double, dz: Double): String =
        "(dx,dy,dz: $dx,$dy,$dz) ".plus(
            player.getNearbyEntities(dx, dy, dz).joinToString(separator = ", ", prefix = "[", postfix = "]") {
                    val type = it.type.name.lowercase().replaceFirstChar { c -> c.uppercase() }
                    if (it.name.equals(it.type.name, ignoreCase = true)) type
                    else "${it.name} ($type)"
                })

    private fun getLookingAt(player: Player, distance: Int): String {
        val targetEntity = player.getTargetEntity(distance)
        return targetEntity?.let { "${it.name} (${it.type.name})" }
            ?: player.getTargetBlock(null, distance).type.name
    }
}

private val instance: PlayerInfoTool = getKoin().get()
val playerInfoTool = tool(instance::getPlayerInfo)
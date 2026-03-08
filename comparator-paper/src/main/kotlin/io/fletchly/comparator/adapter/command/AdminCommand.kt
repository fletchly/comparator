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

package io.fletchly.comparator.adapter.command

import com.mojang.brigadier.Command
import com.mojang.brigadier.context.CommandContext
import io.fletchly.comparator.infra.BukkitPluginRuntime
import io.fletchly.comparator.model.actor.BukkitPlayerActor
import io.fletchly.comparator.model.actor.ConsoleActor
import io.fletchly.comparator.model.command.CommandDefinition
import io.fletchly.comparator.model.command.command
import io.fletchly.comparator.model.message.ChatConversationKey
import io.fletchly.comparator.model.message.ConsoleConversationKey
import io.fletchly.comparator.model.message.PlayerConversationKey
import io.fletchly.comparator.port.`in`.ContextLifecycle
import io.fletchly.comparator.util.toActor
import io.papermc.paper.command.brigadier.CommandSourceStack
import io.papermc.paper.command.brigadier.Commands
import io.papermc.paper.command.brigadier.argument.ArgumentTypes
import io.papermc.paper.command.brigadier.argument.resolvers.selector.PlayerSelectorArgumentResolver
import org.bukkit.permissions.Permission
import org.bukkit.permissions.PermissionDefault

/**
 * Represents the administrative command definition for managing the Comparator system.
 *
 * @param contextLifecycle The utility that performs clearing of conversational contexts for users.
 * @param pluginRuntime The scheduler for managing asynchronous command execution and tasks.
 */
class AdminCommand(
    private val contextLifecycle: ContextLifecycle,
    private val pluginRuntime: BukkitPluginRuntime
) : CommandDefinition {
    override val definition = command("comparator") {
        description = "Manage Comparator"
        permission = "comparator.manage"
        permissionDescription = "Allow a player to manage Comparator"
        permissionDefault = PermissionDefault.TRUE

        val clearSelfPermission = Permission(
            "$permission.clear",
            "Allow a player to clear their chat context",
            PermissionDefault.TRUE
        )

        val clearOtherPermission = Permission(
            "$permission.clear.other",
            "Allow a player to clear others' chat contexts",
            PermissionDefault.OP
        )

        val clearAllPermission = Permission(
            "$permission.clear.all",
            "Allow a player to clear context for all scopes",
            PermissionDefault.OP
        )

        childPermissions = listOf(
            clearSelfPermission,
            clearOtherPermission,
            clearAllPermission
        )

        node {
            then(
                Commands.literal("clear")
                    .requires { it.sender.hasPermission(clearSelfPermission.name) }
                    .executes { ctx ->
                        clearSelf(ctx)
                        Command.SINGLE_SUCCESS
                    }
                    .then(
                        Commands.argument("targets", ArgumentTypes.players())
                            .requires { it.sender.hasPermission(clearOtherPermission.name) && it.sender.hasPermission("minecraft.command.selector") }
                            .executes { ctx ->
                                clearOther(ctx)
                                Command.SINGLE_SUCCESS
                            }
                    )
            ).then(
                Commands.literal("clearConsole")
                    .requires { it.sender.hasPermission(clearOtherPermission.name) }
                    .executes { ctx ->
                        clearConsole(ctx)
                        Command.SINGLE_SUCCESS
                    }
            ).then(
                Commands.literal("clearPublicChat")
                    .requires { it.sender.hasPermission(clearOtherPermission.name) }
                    .executes { ctx ->
                        clearPublicChat(ctx)
                        Command.SINGLE_SUCCESS
                    }
            ).then(
                Commands.literal("clearAll")
                .requires { it.sender.hasPermission(clearAllPermission.name) }
                .executes { ctx ->
                    clearAll(ctx)
                    Command.SINGLE_SUCCESS
                }
            )
        }
    }

    private fun clearSelf(ctx: CommandContext<CommandSourceStack>) {
        val actor = ctx.source.sender.toActor()

        pluginRuntime.runCoroutine {
            contextLifecycle.clearSelf(actor)
        }
    }

    private fun clearOther(ctx: CommandContext<CommandSourceStack>) {
        val targetResolver =
            ctx.getArgument("targets", PlayerSelectorArgumentResolver::class.java)
        val targets = targetResolver.resolve(ctx.source).map { PlayerConversationKey(it.uniqueId) }
        val requestor = ctx.source.sender.toActor()

        pluginRuntime.runCoroutine {
            contextLifecycle.clearOther(requestor, targets)
        }
    }

    private fun clearConsole(ctx: CommandContext<CommandSourceStack>) {
        val requestor = ctx.source.sender.toActor()

        pluginRuntime.runCoroutine {
            when (requestor) {
                is ConsoleActor -> contextLifecycle.clearSelf(requestor)
                is BukkitPlayerActor -> contextLifecycle.clearOther(requestor, listOf(ConsoleConversationKey))
            }
        }
    }

    private fun clearPublicChat(ctx: CommandContext<CommandSourceStack>) {
        val requestor = ctx.source.sender.toActor()

        pluginRuntime.runCoroutine {
            contextLifecycle.clearOther(requestor, listOf(ChatConversationKey))
        }
    }

    private fun clearAll(ctx: CommandContext<CommandSourceStack>) {
        val requestor = ctx.source.sender.toActor()

        pluginRuntime.runCoroutine {
            contextLifecycle.clearAll(requestor)
        }
    }
}
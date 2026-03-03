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
import io.fletchly.comparator.model.command.CommandDefinition
import io.fletchly.comparator.model.command.command
import io.fletchly.comparator.infra.BukkitPluginRuntime
import io.fletchly.comparator.model.user.PublicChatUser
import io.fletchly.comparator.model.user.ConsoleUser
import io.fletchly.comparator.port.`in`.ContextClearer
import io.fletchly.comparator.util.toUser
import io.papermc.paper.command.brigadier.Commands
import io.papermc.paper.command.brigadier.argument.ArgumentTypes
import io.papermc.paper.command.brigadier.argument.resolvers.selector.PlayerSelectorArgumentResolver
import org.bukkit.permissions.Permission
import org.bukkit.permissions.PermissionDefault

/**
 * Represents the administrative command definition for managing the Comparator system.
 *
 * @param contextClearer The utility that performs clearing of conversational contexts for users.
 * @param pluginRuntime The scheduler for managing asynchronous command execution and tasks.
 */
class AdminCommand(
    contextClearer: ContextClearer,
    pluginRuntime: BukkitPluginRuntime
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

        childPermissions = listOf(
            clearSelfPermission,
            clearOtherPermission
        )

        node {
            then(
                Commands.literal("clear")
                    .requires { source ->
                        source.sender.hasPermission(clearSelfPermission.name)
                    }
                    .executes { ctx ->
                        val user = ctx.source.sender.toUser()

                        pluginRuntime.runCoroutine {
                            with(contextClearer) { user.clearSelf() }
                        }

                        Command.SINGLE_SUCCESS
                    }
                    .then(
                        Commands.argument("targets", ArgumentTypes.players())
                            .requires { source ->
                                source.sender.hasPermission(clearOtherPermission.name)
                                        && source.sender.hasPermission("minecraft.command.selector")
                            }
                            .executes { ctx ->
                                val targetResolver =
                                    ctx.getArgument("targets", PlayerSelectorArgumentResolver::class.java)
                                val targetUsers = targetResolver.resolve(ctx.source).map { it.toUser() }
                                val feedbackUser = ctx.source.sender.toUser()

                                pluginRuntime.runCoroutine {
                                    with(contextClearer) { feedbackUser.clearOther(targetUsers) }
                                }

                                Command.SINGLE_SUCCESS
                            }
                    )
            ).then(Commands.literal("clearConsole")
                .requires { source ->
                    source.sender.hasPermission(clearOtherPermission.name)
                }
                .executes { ctx ->
                    val feedbackUser = ctx.source.sender.toUser()

                    pluginRuntime.runCoroutine {
                        with(contextClearer) {
                            when (feedbackUser) {
                                is ConsoleUser -> ConsoleUser.clearSelf()
                                else -> feedbackUser.clearOther(listOf(ConsoleUser))
                            }
                        }
                    }

                    Command.SINGLE_SUCCESS
                }
            ).then(Commands.literal("clearPublicChat")
                .requires { source ->
                    source.sender.hasPermission(clearOtherPermission.name)
                }
                .executes { ctx ->
                    val feedbackUser = ctx.source.sender.toUser()

                    pluginRuntime.runCoroutine {
                        with(contextClearer) {
                            feedbackUser.clearOther(listOf(PublicChatUser))
                        }
                    }

                    Command.SINGLE_SUCCESS
                }
            )
        }
    }
}
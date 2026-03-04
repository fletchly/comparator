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
import com.mojang.brigadier.arguments.StringArgumentType
import io.fletchly.comparator.infra.BukkitPluginRuntime
import io.fletchly.comparator.model.command.CommandDefinition
import io.fletchly.comparator.model.command.command
import io.fletchly.comparator.model.message.Message
import io.fletchly.comparator.port.`in`.MessageSender
import io.fletchly.comparator.util.toUser
import io.papermc.paper.command.brigadier.Commands
import org.bukkit.permissions.PermissionDefault

/**
 * Represents the `/ask` command within the system, which allows users to ask questions.
 *
 * @param messageSender The service responsible for processing and sending user-generated messages.
 * @param pluginRuntime The scheduler utility used for managing asynchronous tasks and coroutines.
 */
class AskCommand(
    messageSender: MessageSender,
    pluginRuntime: BukkitPluginRuntime
) : CommandDefinition {
    override val definition = command("ask") {
        description = "Ask a question"
        aliases = listOf("c")
        permission = ASK_PERMISSION
        permissionDescription = "Allows a player to ask questions"
        permissionDefault = PermissionDefault.TRUE

        node {
            then(
                Commands.argument("prompt", StringArgumentType.greedyString())
                    .executes { ctx ->
                        val prompt = StringArgumentType.getString(ctx, "prompt")
                        val user = ctx.source.sender.toUser()
                        val userMessage = Message.User(prompt, user)

                        pluginRuntime.runCoroutine {
                            messageSender.fromUser(userMessage)
                        }

                        Command.SINGLE_SUCCESS
                    }
            )
        }
    }

    companion object {
        const val ASK_PERMISSION = "comparator.ask"
    }
}
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

package io.fletchly.comparator.model.command

import com.mojang.brigadier.builder.LiteralArgumentBuilder
import io.papermc.paper.command.brigadier.CommandSourceStack
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents
import org.bukkit.permissions.Permission
import org.bukkit.permissions.PermissionDefault
import org.bukkit.plugin.java.JavaPlugin

/**
 * A builder for constructing and configuring a Brigadier-based command with associated metadata and
 * permission handling.
 *
 * @constructor Creates a new command builder for a given command name.
 * @param name The primary name of the command. This is the identifier under which the command will be registered.
 *
 * @property description A brief description of the command's purpose or functionality.
 * @property aliases A list of alternative names for the command. These are additional identifiers under which the command can be executed.
 * @property permission The permission node required to execute this command.
 * @property permissionDescription A detailed description of the permission, outlining its purpose or specific behavior.
 * @property permissionDefault The default behavior of the permission (e.g., granted to operators by default).
 * @property childPermissions Additional permissions related to this command. This allows for fine-grained control over the actions the command provides.
 */
class BrigadierCommandBuilder(private val name: String) {
    var description = ""
    var aliases: List<String> = emptyList()
    var permission = ""
    var permissionDescription = ""
    var permissionDefault: PermissionDefault = PermissionDefault.OP
    var childPermissions: List<Permission> = emptyList()

    private var hasExecutes: Boolean = false
    private var nodeBuilder: LiteralArgumentBuilder<CommandSourceStack> =
        LiteralArgumentBuilder.literal(name)

    fun executes(handler: (CommandSourceStack) -> Int) {
        hasExecutes = true
        nodeBuilder = nodeBuilder.executes { ctx ->
            handler(ctx.source)
        }
    }

    fun node(block: LiteralArgumentBuilder<CommandSourceStack>.() -> Unit) {
        nodeBuilder.apply { block() }
    }

    fun build(): BrigadierCommand {
        require(description.isNotBlank()) { "Command $name must have a description" }
        require(permission.isNotBlank()) { "Command $name must have a permission" }
        require(permissionDescription.isNotBlank()) { "Command $name must have a permission message" }
        require(hasExecutes || nodeBuilder.arguments.isNotEmpty()) {
            "Command $name must have either a default handler or provide arguments"
        }

        nodeBuilder = nodeBuilder.requires { it.sender.hasPermission(permission) }

        return BrigadierCommand(
            node = nodeBuilder.build(),
            description = description,
            aliases = aliases,
            permission = permission,
            permissionDescription = permissionDescription,
            permissionDefault = permissionDefault,
            childPermissions = childPermissions
        )
    }
}

/**
 * Constructs and builds a command using the specified name and configuration block.
 *
 * @param name The primary name of the command. This is used as the identifier for the command during registration.
 * @param block A configuration block for customizing the command. This block is applied to the `BrigadierCommandBuilder`
 *              to set up details such as description, aliases, permissions, and execution logic.
 * @return A fully built and configured `BrigadierCommand` instance, ready for registration and use.
 */
fun command(
    name: String,
    block: BrigadierCommandBuilder.() -> Unit
): BrigadierCommand = BrigadierCommandBuilder(name).apply(block).build()

/**
 * Registers a Brigadier command with the server, adding its associated permissions
 * and handling lifecycle events for command registration.
 *
 * @param cmd The Brigadier command to be registered. Includes the command node,
 * its description, aliases, permissions, and related metadata.
 */
fun JavaPlugin.registerCommand(cmd: BrigadierCommand) {
    server.pluginManager.addPermission(
        Permission(cmd.permission, cmd.permissionDescription, cmd.permissionDefault)
    )

    cmd.childPermissions.forEach { perm ->
        server.pluginManager.addPermission(perm)
    }

    lifecycleManager.registerEventHandler(LifecycleEvents.COMMANDS) {
        it.registrar().register(
            cmd.node,
            cmd.description,
            cmd.aliases
        )
    }
}
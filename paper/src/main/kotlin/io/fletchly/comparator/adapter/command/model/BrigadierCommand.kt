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

package io.fletchly.comparator.adapter.command.model

import com.mojang.brigadier.tree.LiteralCommandNode
import io.papermc.paper.command.brigadier.CommandSourceStack
import org.bukkit.permissions.Permission
import org.bukkit.permissions.PermissionDefault

/**
 * Represents a command registered using the Brigadier library.
 *
 * @property node The root command node that defines the structure and execution logic for the command.
 * @property description A brief description of the command, providing information about its purpose or functionality.
 * @property aliases A list of alternative names for the command, allowing it to be executed under different identifiers.
 * @property permission The permission node required to execute the command.
 * @property permissionDescription A detailed description of the permission, specifying its purpose and any relevant context.
 * @property permissionDefault The default access setting for the command's permission, indicating whether it is granted by default.
 * @property childPermissions A list of additional permissions that are associated with this command, typically for fine-grained control over its functionality.
 */
class BrigadierCommand(
    val node: LiteralCommandNode<CommandSourceStack>,
    val description: String,
    val aliases: List<String>,
    val permission: String,
    val permissionDescription: String,
    val permissionDefault: PermissionDefault,
    val childPermissions: List<Permission>
)
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

package io.fletchly.comparator.adapter.logger

import io.fletchly.comparator.port.out.LogPort
import org.bukkit.plugin.java.JavaPlugin

/**
 * A logger implementation that integrates with the Bukkit plugin's logging system.
 *
 * @constructor Initializes the logger with the provided Bukkit plugin.
 * @param plugin The Bukkit plugin whose logger will be used for logging messages.
 */
class BukkitPluginLogger(plugin: JavaPlugin) : LogPort {
    private val logger = plugin.logger

    override fun info(message: String, source: String?) = logger.info { message.withSource(source) }

    override fun warn(message: String, source: String?) = logger.warning { message.withSource(source) }

    private fun String.withSource(source: String?) = when (source) {
        null -> this
        else -> "[$source] $this"
    }
}
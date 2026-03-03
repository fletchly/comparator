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

package io.fletchly.comparator.adapter.config

import io.fletchly.comparator.model.config.ConfigLoader
import io.fletchly.comparator.model.config.PluginConfig
import io.fletchly.comparator.port.out.LogPort
import org.bukkit.plugin.java.JavaPlugin
import org.spongepowered.configurate.NodePath.path
import org.spongepowered.configurate.transformation.ConfigurationTransformation
import java.nio.file.Path


/**
 * Provides configuration management for the plugin by extending the `HoconConfigService` class
 * specialized for the `PluginConfig` type.
 *
 * This service is responsible for managing the lifecycle of the plugin configuration, including
 * loading, saving, and applying schema migrations. It ensures that the plugin's configuration
 * data is correctly loaded from and saved to a HOCON file stored in the plugin's data folder.
 *
 * The default configuration is maintained and used as a fallback when configuration loading
 * encounters errors. Schema migrations are handled through a versioned transformation defined
 * within the service.
 *
 * @param log A logging interface for recording operations, including warnings and informational messages.
 * @param plugin The JavaPlugin instance whose data folder is used for storing the configuration file.
 */
class PluginConfigService(
    log: LogPort,
    plugin: JavaPlugin,
) : HoconConfigService<PluginConfig>(
    PluginConfig::class,
    Path.of(plugin.dataFolder.path),
    "comparator.conf",
    PluginConfig.Default,
    migrations,
    log
) {
    @Suppress("ObjectPropertyName") // allow more readable transformation names
    private companion object {
        val `1 to 2`: ConfigurationTransformation = ConfigurationTransformation.builder()
            .addAction(path("context")) {_, value ->
                value.node("expire-after-access-minutes").set(10L)
                null
            }.build()

        val `0 to 1`: ConfigurationTransformation = ConfigurationTransformation.builder()
            .addAction(path()) { _, value ->
                value.node("public-chat-prefix").set("@bot")
                null
            }.build()

        val migrations = ConfigurationTransformation.versionedBuilder()
            .versionKey(ConfigLoader.VERSION_KEY)
            .addVersion(2, `1 to 2`)
            .addVersion(1, `0 to 1`)
            .addVersion(0, ConfigurationTransformation.empty())
            .build()
    }
}
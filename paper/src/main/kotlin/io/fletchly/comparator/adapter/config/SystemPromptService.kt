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
import io.fletchly.comparator.model.config.SystemPromptConfig
import io.fletchly.comparator.port.out.LogPort
import io.fletchly.comparator.port.out.SystemConfigPort
import org.bukkit.plugin.java.JavaPlugin
import org.spongepowered.configurate.transformation.ConfigurationTransformation
import java.nio.file.Path

class SystemPromptService(
    log: LogPort,
    plugin: JavaPlugin,
): HoconConfigService<SystemPromptConfig>(
    SystemPromptConfig::class,
    Path.of(plugin.dataFolder.path),
    "system-prompt.conf",
    SystemPromptConfig.Default,
    migrations,
    log
), SystemConfigPort {
    override val prompt = config.prompt

    private companion object {
        val migrations = ConfigurationTransformation.versionedBuilder()
            .versionKey(ConfigLoader.VERSION_KEY)
            .addVersion(0, ConfigurationTransformation.empty())
            .build()
    }
}
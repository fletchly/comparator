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

import io.fletchly.comparator.infra.configurate.HoconConfigLoader
import io.fletchly.comparator.model.config.ConfigResult
import io.fletchly.comparator.model.config.PluginConfig
import io.fletchly.comparator.port.out.LogPort
import org.bukkit.plugin.java.JavaPlugin
import java.nio.file.Path

class PluginConfigService(
    private val log: LogPort,
    plugin: JavaPlugin,
) {
    private val loader = HoconConfigLoader.of<PluginConfig>(Path.of(plugin.dataFolder.path, "comparator.conf"))
    lateinit var config: PluginConfig

    init {
        saveDefault()
        loadConfig()
    }

    fun loadConfig() = when (val result = loader.load()) {
        is ConfigResult.Success -> config = result.config
        is ConfigResult.Failure -> {
            log.warn("${result.error}, falling back to default", this::class.simpleName)
            config = PluginConfig()
        }
    }

    fun saveDefault() = loader.save(PluginConfig()) { error ->
        log.warn("Couldn't save default config: $error", this::class.simpleName)
    }
}
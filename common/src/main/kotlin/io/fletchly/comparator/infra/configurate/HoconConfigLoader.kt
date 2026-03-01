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

package io.fletchly.comparator.infra.configurate

import io.fletchly.comparator.exception.ConfigurationException
import io.fletchly.comparator.model.config.ConfigLoader
import org.spongepowered.configurate.ConfigurateException
import org.spongepowered.configurate.ConfigurationOptions
import org.spongepowered.configurate.kotlin.extensions.get
import org.spongepowered.configurate.transformation.ConfigurationTransformation
import java.nio.file.Path
import kotlin.io.path.exists

class HoconConfigLoader<C : Any>(val path: Path) :
    ConfigLoader<C, ConfigurationTransformation.Versioned> {
    private val loader = ConfigurateLoaders.HOCON(path)

    override fun load(): C = runCatching {
        val root = loader.load()
        root.get() ?: throw ConfigurationException("Config not found")
    }.getOrElse { throw ConfigurationException("Error while loading config file", it) }

    override fun migrate(
        transformation: ConfigurationTransformation.Versioned,
        onMigrate: ((from: Int, to: Int) -> Unit)?
    ) {
        try {
            val root = loader.load()
            val from = transformation.version(root)
            transformation.apply(root)
            val to = transformation.version(root)

            if (from != to) onMigrate?.invoke(from, to)

            loader.save(root)
        } catch (ex: ConfigurateException) {
            throw ConfigurationException("Error while migrating config file", ex)
        }
    }

    override fun save(config: C, header: String?, overwrite: Boolean) {
        if (!overwrite && path.exists()) return
        val root = loader.createNode(
            ConfigurationOptions.defaults().header(header)
        ).node().set(config::class.java, config)
        loader.save(root)
    }
}

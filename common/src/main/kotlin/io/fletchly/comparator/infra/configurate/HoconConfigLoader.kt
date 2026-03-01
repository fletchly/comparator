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
import io.fletchly.comparator.model.config.ConfigResult
import org.spongepowered.configurate.ConfigurateException
import org.spongepowered.configurate.kotlin.extensions.get
import org.spongepowered.configurate.transformation.ConfigurationTransformation
import java.nio.file.Path
import kotlin.io.path.exists
import kotlin.reflect.KClass

class HoconConfigLoader<C : Any>(
    val path: Path, private val type: KClass<C>
) : ConfigLoader<C, ConfigurationTransformation.Versioned> {

    private val loader = ConfigurateLoaders.HOCON(path)

    override fun load(): ConfigResult<out C> = runCatching {
        val config = loader.load().get(type)
            ?: return@runCatching ConfigResult.Failure("Config at '$path' was empty or could not be deserialized into ${type.simpleName}")
        ConfigResult.Success(config)
    }.getOrElse {
        ConfigResult.Failure("Error while loading config file at '$path'")
    }

    override fun migrate(
        transformation: ConfigurationTransformation.Versioned,
        onMigrate: ((from: Int, to: Int) -> Unit)?,
        onFailure: ((String) -> Unit)?
    ) {
        try {
            val root = loader.load()
            val from = transformation.version(root)
            transformation.apply(root)
            val to = transformation.version(root)

            if (from != to) onMigrate?.invoke(from, to)

            loader.save(root)
        } catch (_: ConfigurateException) {
            onFailure?.invoke("Error while migrating config file at '$path'")
        }
    }

    override fun save(
        config: C, header: String?, overwrite: Boolean, onFailure: ((String) -> Unit)?
    ) {
        if (!overwrite && path.exists()) return

        try {
            // Create a root node using the loader's own options so that all
            // registered serializers and settings are inherited correctly.
            val root = loader.createNode().apply {
                header?.let { node().options().header(it) }
                node().set(type.java, config)
            }
            loader.save(root)
        } catch (_: ConfigurateException) {
            onFailure?.invoke("Error while saving config file at '$path'")
        }
    }

    companion object {
        /**
         * Preferred construction path — avoids having to pass [KClass] manually at call sites.
         *
         * ```kotlin
         * val loader = HoconConfigLoader.of<MyConfig>(path)
         * ```
         */
        inline fun <reified C : Any> of(path: Path): HoconConfigLoader<C> = HoconConfigLoader(path, C::class)
    }
}
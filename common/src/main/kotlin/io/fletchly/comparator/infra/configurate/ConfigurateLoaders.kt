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

import org.spongepowered.configurate.hocon.HoconConfigurationLoader
import org.spongepowered.configurate.kotlin.objectMapperFactory
import org.spongepowered.configurate.loader.HeaderMode
import org.spongepowered.configurate.util.MapFactories
import java.nio.file.Path

/**
 * Utility object that provides preconfigured loader builders for the Configurate framework.
 */
object ConfigurateLoaders {
    /**
     * A lambda function for creating a preconfigured instance of [HoconConfigurationLoader].
     *
     * This loader is initialized with:
     * - Header preservation to retain comments from the configuration file.
     * - Pretty-printing enabled with an indent of 2 spaces.
     * - Comments emitted during save operations.
     * - A natural key ordering for maps.
     * - Annotated object serializers registered via [objectMapperFactory].
     *
     * The configuration file path is provided as the input parameter.
     */
    val HOCON: (Path) -> HoconConfigurationLoader = { path ->
        HoconConfigurationLoader.builder()
            .headerMode(HeaderMode.PRESERVE)
            .prettyPrinting(true)
            .indent(2)
            .emitComments(true)
            .defaultOptions { opts ->
                opts.mapFactory(MapFactories.sortedNatural())
                opts.serializers { builder ->
                    builder.registerAnnotatedObjects(objectMapperFactory())
                }
            }
            .path(path)
            .build()
    }
}
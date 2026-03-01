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

package io.fletchly.comparator.model.config

/**
 * A generic interface for handling the lifecycle of configuration objects, including loading,
 * migrating, and saving configurations.
 *
 * Implementations of this interface should define the specific mechanics for interacting
 * with the configuration storage backend and transformation logic.
 *
 * @param C The type of the configuration object being managed.
 * @param T The type of the transformation logic used for migration.
 */
interface ConfigLoader<C, T> {
    fun load(): ConfigResult<out C>

    fun migrate(
        transformation: T,
        onMigrate: ((from: Int, to: Int) -> Unit)? = null,
        onFailure: ((error: String) -> Unit)? = null
    )

    fun save(
        config: C,
        header: String? = null,
        overwrite: Boolean = false,
        onFailure: ((error: String) -> Unit)? = null
    )

    companion object {
        const val VERSION_KEY = "config-version"
    }
}
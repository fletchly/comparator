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
    /**
     * Loads the current configuration object from the underlying storage and returns the result.
     * This method will encapsulate the result as a `ConfigResult` instance to handle both successful
     * and error scenarios.
     *
     * @return A `ConfigResult` containing either the loaded configuration object on success
     *         or an error message on failure.
     */
    fun load(): ConfigResult<out C>

    /**
     * Migrates the configuration to a newer version using the provided transformation logic.
     * This operation may involve updating data structures, converting formats, or applying
     * other necessary modifications to ensure compatibility with a newer version.
     *
     * The operation can optionally provide progress updates and handle failures through
     * callback functions.
     *
     * @param transformation The transformation logic used to migrate the configuration.
     * @param onMigrate A callback function invoked on successful migration. It provides the
     *                  starting version and the target version of the migration.
     * @param onFailure A callback function invoked if the migration process fails. It
     *                  provides an error message describing the failure.
     */
    fun migrate(
        transformation: T,
        onMigrate: ((from: Int, to: Int) -> Unit)? = null,
        onFailure: ((error: String) -> Unit)? = null
    )

    /**
     * Persists the given configuration object to the underlying storage.
     *
     * This method allows specifying a header for the configuration file, determining whether
     * to overwrite existing files, and handling error scenarios through a callback.
     *
     * @param config The configuration object to be saved.
     * @param header An optional header to be included with the configuration file. Defaults to null.
     * @param overwrite A flag indicating whether to overwrite an existing file. Defaults to false.
     * @param onFailure An optional callback invoked if the save operation fails. The callback provides
     *                  an error message describing the failure.
     */
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
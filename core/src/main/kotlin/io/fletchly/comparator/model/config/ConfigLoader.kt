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
interface ConfigLoader<C,T> {
    /**
     * Loads the configuration object from the storage backend.
     *
     * @return The configuration object of type `C`.
     */
    fun load(): C

    /**
     * Executes a migration process for the configuration using the provided transformation logic.
     *
     * This method allows applying transformations to migrate configurations between different
     * versions. Optionally, a callback can be provided to track the migration process by observing
     * source and target version numbers.
     *
     * @param transformation The transformation logic of type `T` applied during the migration process.
     * @param onMigrate An optional callback function invoked with the source (`from`) and target (`to`)
     *                  version numbers during the migration. Default is `null`, meaning no callback is invoked.
     */
    fun migrate(transformation: T, onMigrate: ((from: Int, to: Int) -> Unit)? = null)

    /**
     * Saves the given configuration object to the storage backend.
     *
     * This method allows persisting the configuration, with an option to
     * overwrite the existing configuration in the storage backend.
     *
     * @param config The configuration object of type `C` to be saved.
     * @param overwrite A boolean flag indicating whether to overwrite the
     *                  existing configuration if one exists. Defaults to `false`.
     */
    fun save(config: C, header: String? = null, overwrite: Boolean = false)
}
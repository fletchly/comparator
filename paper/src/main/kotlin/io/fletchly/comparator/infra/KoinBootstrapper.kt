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

package io.fletchly.comparator.infra

import io.fletchly.comparator.di.*
import org.bukkit.plugin.java.JavaPlugin
import org.koin.core.Koin
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import org.koin.java.KoinJavaComponent.getKoin

/**
 * A class responsible for initializing the Koin dependency injection framework within a Bukkit plugin.
 *
 * This class sets up the necessary Koin modules required for the plugin's runtime, infrastructure, adapters,
 * and configuration. It integrates with the plugin's lifecycle and ensures the Koin context is started
 * with the appropriate dependencies.
 *
 * @constructor Creates an instance of KoinBootstrapper.
 * @param plugin The JavaPlugin instance representing the Bukkit plugin.
 */
class KoinBootstrapper(private val plugin: JavaPlugin) {
    private val commonModule = module { includes(commonAdapterModule, commonToolModule) }
    private val paperModule = module { includes(paperInfraModule(plugin), paperConfigModule, paperAdapterModule, paperToolModule) }

    val rootModule = module {
        includes(
            coreModule,
            commonModule,
            paperModule
        )
    }

    /**
     * Starts the Koin dependency injection framework for the associated Bukkit plugin by
     * initializing the necessary modules required for the plugin's runtime, infrastructure,
     * adapters, and configuration.
     *
     * This method invokes the `startKoin` function with a collection of predefined modules.
     * These modules are responsible for configuring the dependency graph and binding the
     * necessary services and components to be used within the plugin.
     *
     * @return The [Koin] instance representing the initialized Koin application context
     *         for dependency injection within the plugin.
     */
    fun start(): Koin {
        startKoin {
            modules(rootModule)
        }

        return getKoin()
    }

    /**
     * Stops the Koin dependency injection framework for the associated Bukkit plugin.
     *
     * This method deactivates the current Koin application context, ensuring that all managed
     * dependencies and related resources are properly released. It is typically invoked during
     * the plugin's shutdown phase to clean up dependency management and prevent resource leaks.
     */
    fun stop() = stopKoin()
}
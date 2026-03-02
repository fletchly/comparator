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

package io.fletchly.comparator.adapter.lifecycle

import io.fletchly.comparator.infra.BukkitPluginRuntime
import io.fletchly.comparator.port.out.ScopePort
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job

/**
 * Adapts the lifecycle of a Bukkit plugin to conform to the [ScopePort] interface.
 *
 * This adapter integrates the plugin's runtime context with coroutine-based task management.
 * It relies on [BukkitPluginRuntime] to provide coroutine lifecycle handling tied to
 * the plugin's lifecycle. This ensures that asynchronous operations are managed in the
 * proper context of the plugin.
 *
 * @constructor Creates an instance of [BukkitPluginLifecycleScope] using a provided
 * [BukkitPluginRuntime] for coroutine execution.
 * @param pluginRuntime The runtime context of the Bukkit plugin, enabling coroutine-based task management.
 */
class BukkitPluginLifecycleScope(
    private val pluginRuntime: BukkitPluginRuntime
) : ScopePort {
    /**
     * Launches a coroutine in the context of the plugin's runtime schedule.
     *
     * This method delegates the execution of the provided suspending block of code
     * to the plugin runtime's coroutine management system. The coroutine will
     * execute within the scope tied to the lifecycle of the plugin, ensuring proper
     * resource management and task monitoring.
     *
     * @param block A suspendable lambda expression representing the coroutine logic
     *              to be executed. The lambda operates within the context of a [CoroutineScope].
     * @return A [Job] representing the executed coroutine. The job can be used to monitor
     *         or control the coroutine's lifecycle, such as cancellation or completion status.
     */
    override fun launch(block: suspend CoroutineScope.() -> Unit): Job = pluginRuntime.runCoroutine(block)
}
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

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import org.bukkit.plugin.java.JavaPlugin

/**
 * A utility class for scheduling and managing tasks and coroutines in the context of a Bukkit plugin.
 *
 * This class provides functionality to run synchronous tasks on the primary server thread, as well as managing
 * coroutines in a dedicated coroutine scope tied to the plugin's lifecycle.
 *
 * @constructor Creates an instance of BukkitPluginRuntime for the given JavaPlugin.
 * @param plugin The plugin instance associated with this scheduler.
 */
class BukkitPluginRuntime(
    private val plugin: JavaPlugin
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    /**
     * Executes a task, ensuring it runs on the primary server thread if required. If already on the primary thread,
     * the task is executed immediately. Otherwise, it is scheduled and resumed asynchronously.
     *
     * This method is useful for safely interacting with Bukkit's API, which requires operations to occur
     * on the server's main thread.
     *
     * @param block The task to execute, represented as a lambda function returning a result of type [T].
     * @return The result of the executed task.
     */
    suspend fun <T> runTask(block: () -> T): T {
        if (plugin.server.isPrimaryThread) return block()
        return suspendCancellableCoroutine { continuation ->
            plugin.server.scheduler.runTask(plugin, Runnable {
                continuation.resumeWith(runCatching(block))
            })
        }
    }

    /**
     * Launches a coroutine within the plugin's dedicated coroutine scope.
     *
     * This method allows for asynchronous execution of tasks within the lifecycle
     * of the associated plugin. The provided suspendable block will be executed in
     * the context of the coroutine scope, which uses a SupervisorJob and the IO dispatcher.
     *
     * @param block A suspendable lambda expression representing the coroutine logic
     * to be executed. The lambda operates within the receiver of a [CoroutineScope].
     */
    fun runCoroutine(block: suspend CoroutineScope.() -> Unit): Job {
        return scope.launch(block = block)
    }


    /**
     * Cancels all scheduled tasks and the associated coroutine scope for the plugin.
     *
     * This method terminates the plugin's coroutine scope, preventing any further
     * asynchronous operations managed by the scope. Additionally, it cancels all
     * tasks scheduled via the Bukkit server's scheduler for the plugin.
     *
     * It is typically invoked during the plugin shutdown phase to ensure that
     * resources and tasks are properly cleaned up and no lingering operations
     * remain active.
     */
    fun cancel() {
        scope.cancel()
        plugin.server.scheduler.cancelTasks(plugin)
    }
}
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

package io.fletchly.comparator.port.`in`

import io.fletchly.comparator.model.actor.Actor

/**
 * Manages the lifecycle operations of a web panel, including starting, stopping,
 * querying status, and restarting the panel.
 */
interface WebPanelLifecycle {
    /**
     * Starts the web panel lifecycle.
     *
     * This method initializes and begins all necessary processes
     * for the operation of the web panel. It ensures the panel is
     * in a ready and functional state for handling incoming tasks
     * or requests.
     */
    suspend fun start(requestor: Actor)

    /**
     * Stops the web panel lifecycle.
     *
     * This method halts all active processes associated with the web panel, effectively
     * transitioning it to a non-operational state. It is typically used to gracefully
     * terminate the panel's operations and release any resources it may be utilizing.
     */
    suspend fun stop(requestor: Actor)


    /**
     * Retrieves the current operational status of the web panel.
     *
     * This method provides information about the state of the web panel,
     * including whether it is running or stopped. It can be used to query
     * the panel's readiness or identify if further lifecycle operations
     * are necessary.
     */
    suspend fun status(requestor: Actor)

    /**
     * Restarts the web panel lifecycle.
     *
     * This method stops and then starts the web panel, effectively reinitializing
     * its state and processes. It is useful for recovering from issues or applying
     * configuration changes without a complete shutdown.
     */
    suspend fun restart(requestor: Actor)

    /**
     * Immediately and forcefully terminates the lifecycle of the web panel.
     *
     * This method halts all operations and processes associated with the web panel
     * without waiting for active requests or tasks to complete. It should be used
     * with caution as it bypasses any graceful shutdown mechanisms, potentially
     * leaving ongoing operations in an incomplete state.
     */
    suspend fun forceStop()
}
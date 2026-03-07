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

/**
 * Provides lifecycle management for the tool registry within the system.
 *
 * This interface is primarily responsible for transitioning the tool registry
 * into a frozen state where no further modifications or registrations are allowed.
 * Freezing the registry ensures that all tools are finalized and ready for execution.
 */
interface ToolRegistryLifecycle {
    /**
     * Transitions the tool registry into a frozen state, preventing further modifications
     * or registrations of tools.
     *
     * This operation finalizes the registry, ensuring that all tools are prepared
     * for execution within the system. Once the registry is frozen, no new tools
     * can be added, and the existing tools cannot be altered.
     */
    fun freeze()
}
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

package io.fletchly.comparator.port.out

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job

/**
 * Represents a port responsible for managing coroutine scopes for launching asynchronous operations tied to a specific lifecycle.
 */
interface ScopePort {
    /**
     * Launches a new coroutine in the scope tied to this implementation. The coroutine will execute the provided
     * suspending block of code. The lifecycle of the coroutine is bound to the scope in which it is launched.
     *
     * @param block The suspending lambda to be launched within a coroutine. It operates in the context of the
     *              [CoroutineScope] tied to this implementation.
     * @return A [Job] representing the coroutine. This can be used to manage or monitor the coroutine's lifecycle.
     */
    fun launch(block: suspend CoroutineScope.() -> Unit): Job
}
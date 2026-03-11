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

import io.fletchly.comparator.model.web.WebPanelMessage

interface WebPanelPort {
    suspend fun start(): WebPanelMessage
    suspend fun stop(graceMs: Long, timeoutMs: Long): WebPanelMessage
    suspend fun restart(onStop: suspend (WebPanelMessage) -> Unit, onStart: suspend (WebPanelMessage) -> Unit)
    suspend fun status(): WebPanelMessage
    suspend fun forceStop()
}
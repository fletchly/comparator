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

package io.fletchly.comparator.model.options

/**
 * Configuration options for managing conversational contexts within the application.
 *
 * @property conversationMessageLimit The maximum number of messages allowed in a single conversation context.
 *                                    When the limit is reached, the oldest messages are removed to accommodate new ones.
 */
data class ContextOptions(
    val conversationMessageLimit: Int,
    val expireAfterAccessMinutes: Long
)
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

package io.fletchly.comparator.model.scope

import java.util.*

/**
 * Represents a conversational context for a user within the system.
 *
 * This interface defines the core attributes associated with a user
 * participating in a conversation or other interactive exchanges.
 *
 * It provides a foundation for identifying users and determining their
 * availability within the system.
 */
interface ConversationScope {
    val displayName: String
    val uniqueId: UUID
    val isOnline: Boolean
}
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

package io.fletchly.comparator.model.user

import java.util.*

/**
 * Represents a user within the system with basic identifying information and operational privileges.
 *
 * This interface is primarily used to define users that interact with the system in various contexts,
 * such as sending messages, receiving notifications, and participating in conversations.
 *
 * @property displayName The name to be displayed for the user.
 * @property uniqueId A universally unique identifier (UUID) representing the user.
 * @property isOp A flag indicating whether the user has operator (admin) privileges.
 */
interface User {
    val displayName: String
    val uniqueId: UUID
    val isOp: Boolean
    val isOnline: Boolean
}
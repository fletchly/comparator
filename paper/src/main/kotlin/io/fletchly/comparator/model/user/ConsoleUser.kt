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

import java.util.UUID

/**
 * A singleton implementation of the [User] interface that represents the console as a system user.
 *
 * This class is used to identify and interact with the console within the system, providing a unique identifier,
 * a display name, and operator privileges. The console is always considered an operator and has a fixed UUID
 * that serves as its unique identifier.
 *
 * This implementation ensures that the console is treated as a distinct user entity, enabling seamless interaction
 * with systems or contexts that require a `User` instance.
 */
object ConsoleUser: User {
    override val displayName: String = "Console"
    override val uniqueId: UUID = UUID.nameUUIDFromBytes("CONSOLE".toByteArray())
    override val isOnline = true
}
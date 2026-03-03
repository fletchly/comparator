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
 * A singleton implementation of the `User` interface that represents a public chat user within the system.
 *
 * This implementation is used specifically to model interactions with a shared or public chat system, providing
 * a fixed display name, a unique identifier, and constant online status.
 *
 * The `PublicChatUser` instance facilitates seamless integration of the public chat as a recognizable `User`
 * entity, ensuring compatibility with systems or contexts where `User` instances are required.
 */
object PublicChatUser: User {
    override val displayName = "Public Chat"
    override val uniqueId: UUID = UUID.nameUUIDFromBytes("CHAT".toByteArray())
    override val isOnline = true
}
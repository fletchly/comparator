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

package io.fletchly.comparator.model.actor

import io.fletchly.comparator.model.actor.ConsoleActor.conversationKey
import io.fletchly.comparator.model.actor.ConsoleActor.displayName
import io.fletchly.comparator.model.actor.ConsoleActor.isOnline
import io.fletchly.comparator.model.message.ConsoleConversationKey
import io.fletchly.comparator.model.message.ConversationKey

/**
 * Represents an actor that interacts with the system through the console.
 *
 * This singleton implementation of the `Actor` interface is used to represent
 * a system-level participant, such as a console-based administrator or automated entity.
 * It provides a constant display name, a predefined conversation key, and a persistent
 * online status.
 *
 * @property displayName The name displayed for the console actor. This is always "Console".
 * @property conversationKey The key used to identify the console's participation in conversations.
 * @property isOnline Indicates whether the console actor is online. This is always `true`.
 */
object ConsoleActor : Actor {
    override val displayName: String = "Console"
    override val conversationKey: ConversationKey = ConsoleConversationKey
    override val isOnline: Boolean = true
}
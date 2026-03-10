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

package io.fletchly.comparator.model.message

import io.fletchly.comparator.model.message.ConsoleConversationKey.uniqueId
import java.util.*

/**
 * Represents a conversation key uniquely associated with the console.
 *
 * This object implements the `ConversationKey` interface and serves as an identifier
 * for console-based conversations in the system. The unique identifier is generated
 * using a fixed string ("CONSOLE") to ensure that it corresponds specifically to the
 * console's conversation scope.
 *
 * @property uniqueId The unique identifier for the console conversation, derived from
 *                    a name-based `UUID` generated with the string "CONSOLE".
 */
object ConsoleConversationKey : ConversationKey() {
    override val uniqueId: UUID = UUID.nameUUIDFromBytes("CONSOLE".toByteArray())
}
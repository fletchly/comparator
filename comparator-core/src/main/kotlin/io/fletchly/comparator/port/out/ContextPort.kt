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

import io.fletchly.comparator.model.message.Conversation
import io.fletchly.comparator.model.message.ConversationKey
import io.fletchly.comparator.model.message.Message

/**
 * Defines a port for managing user-specific conversational contexts.
 */
interface ContextPort {
    /**
     * Retrieves the conversation associated with the specified key.
     *
     * @param key The unique identifier for the conversation to be retrieved.
     * @return The conversation corresponding to the provided key.
     */
    suspend fun get(key: ConversationKey): Conversation

    /**
     * Appends a message to the conversation identified by the given key.
     *
     * @param key The unique identifier for the conversation to which the message will be appended.
     * @param message The message to be appended to the conversation.
     */
    suspend fun append(key: ConversationKey, message: Message)

    /**
     * Clears the conversation associated with the specified key.
     *
     * This method removes the conversation for the given key from the system,
     * effectively resetting any associated state or messages that have been exchanged.
     *
     * @param key The unique identifier for the conversation to be cleared.
     */
    suspend fun clear(key: ConversationKey)

    /**
     * Clears all conversations across the system.
     *
     * This method performs a global reset of all conversations, removing
     * any associated state or messages. It is intended for use in scenarios
     * where all conversational contexts need to be purged simultaneously.
     */
    suspend fun clearAll()
}
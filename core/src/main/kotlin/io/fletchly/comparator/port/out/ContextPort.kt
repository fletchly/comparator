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
import io.fletchly.comparator.model.message.Message
import io.fletchly.comparator.model.user.ConversationScope

/**
 * Defines a port for managing user-specific conversational contexts.
 */
interface ContextPort {
    /**
     * Retrieves the conversational context associated with a specific scope.
     *
     * @param scope The scope whose conversation context is to be retrieved.
     * @return The conversation associated with the specified scope.
     */
    suspend fun get(scope: ConversationScope): Conversation

    /**
     * Appends a message to the conversational context associated with a scope.
     *
     * @param scope The scope whose conversation context is being updated.
     * @param message The message to be appended to the scope's conversation context.
     */
    suspend fun append(scope: ConversationScope, message: Message)

    /**
     * Clears the conversational context associated with the specified scope.
     *
     * @param scope The scope whose conversational context is to be cleared.
     */
    suspend fun clear(scope: ConversationScope)

    /**
     * Clears all conversational contexts managed by the implementation.
     */
    suspend fun clearAll()
}
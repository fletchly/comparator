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

package io.fletchly.comparator.port.`in`

import io.fletchly.comparator.model.message.Conversation
import io.fletchly.comparator.model.message.ConversationKey
import io.fletchly.comparator.model.tool.Tool

/**
 * Defines the contract for managing conversations and tools in a panel.
 *
 * This interface provides methods for retrieving, clearing, and managing
 * conversational data and tools within the system. It allows interaction
 * with multiple conversations and tools, enabling their accessibility and
 * lifecycle management.
 */
interface PanelData {
    /**
     * Retrieves all existing conversations within the system.
     *
     * This method returns a map where each key represents a unique identifier for
     * a conversation, and each value contains the corresponding conversation data.
     *
     * @return A map containing conversation keys and their associated conversations.
     */
    suspend fun getAllConversations(): Map<ConversationKey, Conversation>

    /**
     * Retrieves a specific conversation using the provided unique key.
     *
     * This method fetches the conversation that matches the given key, allowing
     * access to the sequence of messages exchanged within that conversation.
     *
     * @param key The unique identifier for the conversation to be retrieved.
     * @return The conversation associated with the provided key.
     */
    suspend fun getConversation(key: ConversationKey): Conversation

    /**
     * Clears all conversations within the system.
     *
     * This method removes all existing conversational data, including their associated
     * states, from the underlying storage or memory. It is typically used to reset
     * the system's conversational state entirely.
     *
     * Note: This action cannot be undone and will result in the loss of all
     * data related to current conversations.
     */
    suspend fun clearAllConversations()

    /**
     * Removes a specific conversation identified by the provided key.
     *
     * This method deletes all messages and associated data related to the conversation
     * matching the given key. It is used to clear the state of a single conversation
     * while leaving other conversations unaffected.
     *
     * @param key The unique identifier of the conversation to be cleared.
     */
    suspend fun clearConversation(key: ConversationKey)


    /**
     * Gets all registered tools
     *
     * @return list of registered tools
     */
    suspend fun getAllTools(): List<Tool>

    /**
     * Get tool by name
     *
     * @param name tool name
     * @return found tool, null if not found
     */
    suspend fun getTool(name: String): Tool?

    suspend fun getVersion(): String
}
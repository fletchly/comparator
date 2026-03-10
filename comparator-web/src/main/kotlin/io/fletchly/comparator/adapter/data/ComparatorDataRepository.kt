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

package io.fletchly.comparator.adapter.data

import io.fletchly.comparator.model.dto.ConversationDto
import io.fletchly.comparator.model.dto.MessageDto
import io.fletchly.comparator.model.dto.ToolDto
import io.fletchly.comparator.model.dto.toDto
import io.fletchly.comparator.model.message.BasicConversationKey
import io.fletchly.comparator.model.message.ChatConversationKey
import io.fletchly.comparator.model.message.ConsoleConversationKey
import io.fletchly.comparator.model.message.Conversation
import io.fletchly.comparator.model.message.Message
import io.fletchly.comparator.port.`in`.WebPanelData
import io.fletchly.comparator.port.out.DataRepositoryPort
import java.util.*

class ComparatorDataRepository(
    private val data: WebPanelData
) : DataRepositoryPort {
    override suspend fun getAllConversations(): Map<String, ConversationDto> =
        data.getAllConversations()
            .map { (key, value) ->
                key.uniqueId.toString() to ConversationDto(
                    displayName = key.displayName,
                    messages = value.messages.map { it.toDto() }
                )
            }
            .toMap()

    override suspend fun getAllPlayerConversations(): Map<String, ConversationDto> =
        data.getAllConversations()
            .filter { (key, _) ->
                key != ChatConversationKey && key != ConsoleConversationKey
            }
            .map { (key, value) ->
                key.uniqueId.toString() to ConversationDto(
                    displayName = key.displayName,
                    messages = value.messages.map { it.toDto() }
                )
            }
            .toMap()

    override suspend fun getConversation(key: UUID): ConversationDto =
        data.getConversation(BasicConversationKey(key)).let {
            ConversationDto(
                displayName = it.ownerDisplayName(),
                messages = it.messages.map { m -> m.toDto() }
            )
        }

    override suspend fun clearAllConversations() {
        data.clearAllConversations()
    }

    override suspend fun clearConversation(key: UUID) {
        data.clearConversation(BasicConversationKey(key))
    }

    override suspend fun getAllTools(): List<ToolDto> =
        data.getAllTools().map { it.toDto() }

    override suspend fun getTool(name: String): Result<ToolDto> = runCatching {
        data.getTool(name)?.toDto() ?: throw NoSuchElementException("Tool not found")
    }

    private fun Conversation.ownerDisplayName(): String? =
        messages.filterIsInstance<Message.User>().firstOrNull()?.actor?.displayName
}
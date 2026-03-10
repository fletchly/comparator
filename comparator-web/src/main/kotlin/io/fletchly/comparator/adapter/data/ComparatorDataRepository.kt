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

import io.fletchly.comparator.model.dto.MessageDto
import io.fletchly.comparator.model.dto.ToolDto
import io.fletchly.comparator.model.dto.toDto
import io.fletchly.comparator.model.message.BasicConversationKey
import io.fletchly.comparator.port.`in`.WebPanelData
import io.fletchly.comparator.port.out.DataRepositoryPort
import java.util.*

class ComparatorDataRepository(
    private val data: WebPanelData
) : DataRepositoryPort {
    override suspend fun getAllConversations(): Map<String, List<MessageDto>> =
        data.getAllConversations().map { (key, value) ->
            key.uniqueId.toString() to value.messages.map { it.toDto() }
        }.toMap()

    override suspend fun getConversation(key: UUID): List<MessageDto> =
        data.getConversation(BasicConversationKey(key)).messages.map { it.toDto() }

    override suspend fun getAllTools(): List<ToolDto> =
        data.getAllTools().map { it.toDto() }

    override suspend fun getTool(name: String): Result<ToolDto> = runCatching {
        data.getTool(name)?.toDto() ?: throw NoSuchElementException("Tool not found")
    }
}
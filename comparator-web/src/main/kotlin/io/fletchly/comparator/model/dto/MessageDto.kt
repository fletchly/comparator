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

package io.fletchly.comparator.model.dto

import io.fletchly.comparator.model.message.Conversation
import io.fletchly.comparator.model.message.Message
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed interface MessageDto {
    val content: String

    @Serializable
    @SerialName("user")
    data class User(
        override val content: String,
        val name: String
    ) : MessageDto

    @Serializable
    @SerialName("assistant")
    data class Assistant(
        override val content: String,
        val toolCalls: List<ToolCallDto>?
    ) : MessageDto

    @Serializable
    @SerialName("tool")
    data class Tool(
        override val content: String,
        val name: String
    ) : MessageDto
}

fun Message.toDto() = when (this) {
    is Message.User -> MessageDto.User(this.content, this.actor.displayName)
    is Message.Assistant -> MessageDto.Assistant(this.content, this.toolCalls?.map { it.toDto() })
    is Message.Tool -> MessageDto.Tool(this.content, this.name)
}

fun Conversation.toDto(): List<MessageDto> = this.messages.map { it.toDto() }
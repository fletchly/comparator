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

package io.fletchly.comparator.adapter.ollama.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Chat history message
 *
 * @see <a href="https://docs.ollama.com/api/chat">Ollama Chat API docs</a>
 *
 * @property content message text content
 * @property role author of the message.
 */
@Serializable
sealed interface ChatMessage {
    val content: String
    val role: String

    /**
     * User message
     *
     * @property role always `user`
     */
    @Serializable
    data class User(
        override val content: String,
    ) : ChatMessage {
        override val role: String = "user"
    }

    /**
     * Assistant message
     *
     * @property toolCalls tool call requests produced by the model
     * @property role always `assistant`
     */
    @Serializable
    data class Assistant(
        override val content: String,

        @SerialName("tool_calls")
        val toolCalls: List<ChatToolCall>? = null
    ) : ChatMessage {
        override val role: String = "assistant"
    }

    /**
     * ChatTool message
     *
     * @property role always `tool`
     */
    @Serializable
    data class Tool(
        override val content: String,
    ) : ChatMessage {
        override val role: String = "tool"
    }

    /**
     * System message
     *
     * @property role always `system`
     */
    @Serializable
    data class System(
        override val content: String,
    ) : ChatMessage {
        override val role: String = "system"
    }
}

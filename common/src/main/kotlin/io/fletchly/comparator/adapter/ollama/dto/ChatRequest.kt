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

import kotlinx.serialization.Serializable

/**
 * Represents a request for a chat interaction, containing the necessary parameters
 * and configurations for generating a response.
 *
 * @see <a href="https://docs.ollama.com/api/chat">Ollama Chat API docs</a>
 *
 * @property model model identifier
 * @property messages chat history as an array of message objects (each with a role and content)
 * @property tools optional list of function tools the model may call during the chat
 * @property options runtime options that control text generation
 */
@Serializable
data class ChatRequest(
    val model: String,
    val messages: List<ChatMessage>,
    val tools: List<ChatTool>?,
    val options: ChatOptions,
) {
    @Suppress("unused")
    val stream = false

    @Suppress("unused")
    val think = false
}


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
import io.fletchly.comparator.model.message.MessageResult

/**
 * An interface representing the core functionality for generating responses in a conversational system.
 */
interface AIPort {
    /**
     * Generates a response from the AI assistant based on the provided system prompt and current conversation state.
     *
     * @param systemPrompt The system-level instruction or context used as input for generating the response.
     * @param conversation The current state of the conversation, containing the history of messages exchanged.
     * @return A [MessageResult] that indicates either a successful assistant response or an error.
     */
    suspend fun generateResponse(systemPrompt: String, conversation: Conversation): MessageResult<Message.Assistant>
}
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

import kotlin.collections.emptyList

/**
 * Represents a series of messages exchanged in a conversational system.
 *
 * The `Conversation` class serves as an abstraction for managing the flow
 * of messages, including messages sent by users, responses generated
 * by an AI assistant, and outputs from tool executions.
 *
 * @property conversation The collection of messages that form the conversation.
 */
data class Conversation(private val conversation: ArrayDeque<Message>) {
    val size get() = conversation.size
    val messages get() = conversation.toList()

    fun add(message: Message) {
        conversation.add(message)
    }

    fun removeOldest() {
        conversation.removeFirst()
    }
}

/**
 * Creates a `Conversation` instance from a variable number of `Message` objects.
 * The provided messages are used to initialize the conversation's message list.
 *
 * @param messages A variable number of `Message` objects representing
 *                 the initial set of messages in the conversation.
 *                 These messages can be user-generated, AI responses, or tool outputs.
 * @return A `Conversation` instance containing the provided messages.
 */
fun conversationOf(vararg messages: Message) = Conversation(ArrayDeque(messages.toList()))
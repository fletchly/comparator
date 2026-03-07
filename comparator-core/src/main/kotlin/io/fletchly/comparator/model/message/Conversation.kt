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
    @get:Synchronized
    val size get() = conversation.size

    @get:Synchronized
    val messages get() = conversation.toList()

    /**
     * Adds a new message to the conversation.
     *
     * @param message The message to be added to the conversation.
     */
    fun add(message: Message) {
        conversation.add(message)
    }

    /**
     * Adds a new message to the conversation and ensures the size of the conversation
     * does not exceed the specified limit. If the limit is reached, the oldest message
     * in the conversation is removed.
     *
     * @param message The message to be added to the conversation.
     * @param limit The maximum number of messages allowed in the conversation. If this
     *              limit is exceeded, the oldest message will be removed to maintain
     *              the size constraint.
     */
    @Synchronized
    fun addAndTrim(message: Message, limit: Int) {
        conversation.add(message)
        if (conversation.size >= limit) {
            conversation.removeFirst()
        }
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
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

import io.fletchly.comparator.model.message.Message

/**
 * Represents a contract for sending user-generated messages within the system.
 *
 * This interface defines a method for handling messages originating from a user
 * and ensures appropriate processing of these messages within the system's
 * conversational context.
 */
interface MessageSender {
    /**
     * Handles a message sent by a user within the conversational system.
     *
     * @param message The user-generated message to be processed. The message
     *                includes its content and the sender's information.
     */
    suspend fun fromUser(message: Message.User)
}
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

import io.fletchly.comparator.model.actor.Actor
import io.fletchly.comparator.model.scope.ConversationScope

/**
 * Defines a port for sending notification messages to users.
 */
interface NotificationPort {

    /**
     * Sends an informational notification message to the specified target actor.
     *
     * This method is used to deliver non-critical information to a specific recipient.
     *
     * @param target The recipient actor who will receive the informational message.
     * @param message The content of the informational message to be sent.
     */
    suspend fun info(target: Actor, message: String)


    /**
     * Sends an error notification message to the specified target actor.
     *
     * This method is used to deliver critical or error-related messages
     * to a specific recipient, typically to inform them of an issue or failure.
     *
     * @param target The recipient actor who will receive the error message.
     * @param message The content of the error message to be sent.
     */
    suspend fun error(target: Actor, message: String)
}
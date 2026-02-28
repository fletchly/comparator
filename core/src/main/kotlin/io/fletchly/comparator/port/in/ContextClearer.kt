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

import io.fletchly.comparator.model.user.User

/**
 * Provides an interface for clearing conversation context for one or more actors
 * in a conversational system.
 */
interface ContextClearer {
    /**
     * Clears the conversational context for the specified target users.
     *
     * @param target A vararg parameter representing the users whose context
     *               should be cleared.
     */
    suspend fun clear(vararg target: User)

    /**
     * Clears the conversational context for the specified target users and notifies the sender.
     *
     * @param sender The user initiating the clear operation, typically for notification or logging purposes.
     * @param target A vararg parameter representing the users whose context should be cleared.
     */
    suspend fun clearWithFeedback(sender: User, vararg target: User)

    /**
     * Clears all conversational contexts within the system.
     */
    suspend fun clearAll()
}
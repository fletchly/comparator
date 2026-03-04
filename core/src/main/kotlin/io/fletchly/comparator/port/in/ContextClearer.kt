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

import io.fletchly.comparator.model.user.RestrictedConversationScope
import io.fletchly.comparator.model.user.ConversationScope

/**
 * Provides an interface for clearing conversation context for one or more scopes
 * in a conversational system.
 */
interface ContextClearer {


    /**
     * Clears the conversational context for the current `RestrictedConversationScope` instance.
     *
     * This method removes any conversation-specific data or state tied to the specific user
     * or group represented by the invoking `RestrictedConversationScope`. It ensures that
     * any ongoing conversational context is reset, effectively providing a clean slate for
     * future interactions.
     *
     * Designed for scenarios where the conversational context of a single actor is to be
     * cleared, such as when a user leaves the system or resets their session.
     */
    suspend fun RestrictedConversationScope.clearSelf()


    /**
     * Clears the conversational context for the specified list of targets.
     *
     * This method removes any conversation-specific data or state tied to the provided
     * `ConversationScope` instances. It is used to reset the conversational context for
     * specific users or groups, ensuring that they start with a clean slate in the system.
     *
     * @param targets A list of `ConversationScope` objects representing the users or groups
     *                whose conversational context should be cleared.
     */
    suspend fun RestrictedConversationScope.clearOther(targets: List<ConversationScope>)

    /**
     * Clears all conversational contexts within the system.
     */
    suspend fun clearAll()
}
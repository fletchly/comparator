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

import io.fletchly.comparator.model.actor.Actor
import io.fletchly.comparator.model.message.ConversationKey

/**
 * Provides an interface for clearing conversation context for one or more scopes
 * in a conversational system.
 */
interface ContextLifecycle {

    /**
     * Clears the conversational context for the specified actor.
     *
     * This method removes conversation-specific data or state associated with
     * the given `Actor` instance. It is typically used to reset the conversational
     * context for the actor, ensuring that the system no longer retains any history
     * or state tied to the actor's participation in previous interactions.
     *
     * @param target The actor whose conversational context should be cleared.
     */
    suspend fun clearSelf(target: Actor)

    /**
     * Clears the conversational context for the specified targets as requested by a given actor.
     *
     * This method removes conversation-specific data or state associated with the provided
     * list of targets, ensuring that the system no longer retains history or context
     * tied to their interactions. It is typically used to allow the requestor to reset
     * or clear contexts affecting other actors identified by the given conversation keys.
     *
     * @param requestor The actor initiating the context clearing operation.
     * @param targets A list of conversation keys representing the targets whose conversational
     *                contexts should be cleared.
     */
    suspend fun clearOther(requestor: Actor, targets: List<ConversationKey>)

    /**
     * Clears the conversational context for all actors in the system as requested by the specified actor.
     *
     * This method removes all conversation-specific data or state associated with all actors
     * within the system. It is typically used to perform a global reset of the conversational
     * context at the request of the provided actor, ensuring no history or state is retained.
     *
     * @param requestor The actor initiating the context clearing operation for all actors.
     */
    suspend fun clearAll(requestor: Actor)

    /**
     * Clears all conversational contexts within the system.
     */
    suspend fun clearFull()
}
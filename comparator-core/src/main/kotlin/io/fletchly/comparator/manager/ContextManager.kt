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

package io.fletchly.comparator.manager

import io.fletchly.comparator.model.actor.Actor
import io.fletchly.comparator.model.message.ConversationKey
import io.fletchly.comparator.port.`in`.ContextLifecycle
import io.fletchly.comparator.port.out.ContextPort
import io.fletchly.comparator.port.out.LogPort
import io.fletchly.comparator.port.out.NotificationPort
import io.fletchly.comparator.util.pluralize

/**
 * Manages the context and notification operations for a given set of actors in a conversational system.
 *
 * This class provides functionality to clear context information for one or multiple actors
 * and to notify the sender of any successful context clear operations. It interacts with the
 * context storage and notification mechanisms through the provided `ContextPort` and `NotificationPort` interfaces.
 *
 * @constructor Creates a `ContextManager` with a specified context storage and notification mechanism.
 *
 * @param context The storage mechanism for managing conversation contexts.
 * @param notification The notification mechanism for sending updates to actors.
 */
class ContextManager(
    private val context: ContextPort,
    private val notification: NotificationPort,
    private val log: LogPort
) : ContextLifecycle {
    override suspend fun clearSelf(target: Actor) {
        context.clear(target.conversationKey)
        log.info("Cleared chat context for ${target.displayName}", ContextManager::class.simpleName)
        notification.info(target, "Cleared chat context")
    }

    override suspend fun clearOther(
        requestor: Actor,
        targets: List<ConversationKey>
    ) {
        targets.forEach {
            context.clear(it)
        }

        val message = "Cleared chat context for ${targets.size} ${"target".pluralize(targets.size)}"
        log.info(message, ContextManager::class.simpleName)
        notification.info(requestor, message)
    }

    override suspend fun clearAll(requestor: Actor) {
        context.clearAll()
        val message = "Cleared chat context for all scopes"
        log.info(message, ContextManager::class.simpleName)
        notification.info(requestor, message)
    }

    override suspend fun clearFull() {
        context.clearAll()
    }
}
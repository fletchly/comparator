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

package io.fletchly.comparator.adapter.persistence

import io.fletchly.comparator.model.message.Conversation
import io.fletchly.comparator.model.message.Message
import io.fletchly.comparator.model.message.conversationOf
import io.fletchly.comparator.model.user.ConversationScope
import io.fletchly.comparator.port.out.ContextPort
import io.fletchly.comparator.model.options.ContextOptions
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.util.*

/**
 * An implementation of `ContextPort` that stores conversational contexts for users in memory.
 *
 * This class provides methods to manage user-specific conversational states, allowing for
 * retrieval, modification, and deletion of conversations. It uses an in-memory
 * `HashMap` to store the contexts, identified by user UUIDs, and ensures thread safety
 * via a `Mutex`.
 *
 * @constructor Initializes the `InMemoryContextStore` with the provided configuration options.
 * @param config Configuration options that determine operational parameters such as the
 *               maximum allowed messages per conversation.
 */
@Deprecated("Use CaffeineContextStore for better concurrency", ReplaceWith("CaffeineContextStore(config)"))
class InMemoryContextStore(private val config: ContextOptions) : ContextPort {
    private val mutex = Mutex()
    private val context = HashMap<UUID, Conversation>()

    override suspend fun get(scope: ConversationScope): Conversation = mutex.withLock {
        context[scope.uniqueId] ?: conversationOf()
    }

    override suspend fun append(scope: ConversationScope, message: Message): Unit = mutex.withLock {
        val conversation = context.getOrPut(scope.uniqueId) { conversationOf() }
        conversation.add(message)
        if (conversation.size >= config.conversationMessageLimit) {
            conversation.removeOldest()
        }
    }

    override suspend fun clear(scope: ConversationScope): Unit = mutex.withLock {
        context.remove(scope.uniqueId)
    }

    override suspend fun clearAll() = mutex.withLock {
        context.clear()
    }
}
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

import com.github.benmanes.caffeine.cache.Caffeine
import io.fletchly.comparator.model.message.Conversation
import io.fletchly.comparator.model.message.ConversationKey
import io.fletchly.comparator.model.message.Message
import io.fletchly.comparator.model.message.conversationOf
import io.fletchly.comparator.model.options.ContextOptions
import io.fletchly.comparator.port.out.ContextPort
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * An implementation of the `ContextPort` interface that uses a Caffeine cache
 * to manage user-specific conversational contexts.
 *
 * The `CaffeineContextStore` provides mechanisms for storing, retrieving,
 * updating, and clearing conversations associated with users. It leverages
 * a Caffeine cache for efficient memory management and supports expiring
 * conversation contexts after a specified duration of inactivity.
 *
 * @constructor Initializes the context store with the provided configuration options.
 * @param config Configuration options defining the expiration time and
 *               conversation message limit for the stored contexts.
 */
class CaffeineContextStore(private val config: ContextOptions) : ContextPort {
    private val context = Caffeine.newBuilder()
        .expireAfterAccess(config.expireAfterAccessMinutes, TimeUnit.MINUTES)
        .build<UUID, Conversation>()

    override suspend fun get(key: ConversationKey): Conversation {
        return context.getIfPresent(key.uniqueId) ?: conversationOf()
    }

    override suspend fun append(key: ConversationKey, message: Message) {
        val conversation = context.get(key.uniqueId) { conversationOf() }
        conversation.addAndTrim(message, config.conversationMessageLimit)
    }

    override suspend fun clear(key: ConversationKey) {
        context.invalidate(key.uniqueId)
    }

    override suspend fun clearAll() {
        context.invalidateAll()
    }
}
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

package io.fletchly.comparator.model.user

import java.util.*

/**
 * A singleton implementation of the [ConversationScope] interface that represents a public chat context.
 *
 * This class is used to identify and interact with a public chat within the system, providing a fixed
 * display name, a unique identifier, and an indication of its always-online status. The singleton nature
 * of this implementation ensures that the public chat is treated as a shared conversational context
 * for system-wide interactions.
 *
 * This scope is commonly used when broadcasting messages or interacting with a general audience
 * rather than individual users or specific groups.
 */
object PublicChatConversationScope : ConversationScope {
    override val displayName = "Public Chat"
    override val uniqueId: UUID = UUID.nameUUIDFromBytes("CHAT".toByteArray())
    override val isOnline = true
}
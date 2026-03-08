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

import java.util.*

/**
 * Represents a conversation key uniquely associated with a specific player.
 *
 * This value class implements the `ConversationKey` interface and is used to
 * establish a unique identifier for a player's participation in a conversation.
 * The key is based on a `UUID`, which ensures global uniqueness.
 *
 * @property uniqueId The unique identifier for the player.
 */
@JvmInline
value class PlayerConversationKey(override val uniqueId: UUID) : ConversationKey
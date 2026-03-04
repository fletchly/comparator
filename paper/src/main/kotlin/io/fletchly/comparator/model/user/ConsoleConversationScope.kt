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
 * A singleton implementation of the [RestrictedConversationScope] interface representing a console-based
 * conversational context.
 *
 * This class is used to identify and interact with the console as a distinct conversation scope in the system.
 * It provides a fixed display name, a unique identifier based on the string "CONSOLE", and status information
 * indicating that the console is always considered online.
 *
 * This scope is typically used for system-level communications with administrators or for actions initiated
 * directly through server commands.
 */
object ConsoleConversationScope : RestrictedConversationScope {
    override val displayName: String = "Console"
    override val uniqueId: UUID = UUID.nameUUIDFromBytes("CONSOLE".toByteArray())
    override val isOnline = true
}
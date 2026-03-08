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

package io.fletchly.comparator.model.tool

import org.bukkit.entity.Player

@RequiresOptIn(
    message = "Use platform-specific extensions like `bukkitPlayer` instead of calling getPlayer() directly.",
    level = RequiresOptIn.Level.ERROR
)
@Target(AnnotationTarget.FUNCTION)
annotation class InternalToolContextApi

/**
 * Represents a context object that provides contextual data to tools during execution.
 * Implementations of this interface may supply platform-specific information,
 * such as a reference to the player executing a command or other relevant data.
 */
interface ToolContext {
    @InternalToolContextApi
    fun <T: Any> getPlayer() : T?
}

/**
 * Provides a convenient extension property to retrieve the Bukkit `Player` instance, if available,
 * associated with a given `ToolContext`.
 *
 * This property serves as a platform-specific accessor that abstracts the underlying `getPlayer` method.
 * If no `Player` instance is linked to the context, the value will be `null`.
 *
 * @receiver ToolContext The context object that may provide access to a `Player` instance.
 * @return The associated `Player` instance if available; otherwise, `null`.
 */
@OptIn(InternalToolContextApi::class)
val ToolContext.bukkitPlayer: Player?
    get() = getPlayer()
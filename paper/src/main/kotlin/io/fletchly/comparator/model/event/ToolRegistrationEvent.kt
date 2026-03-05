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

package io.fletchly.comparator.model.event

import io.fletchly.comparator.tool.ToolRegistry
import org.bukkit.event.Event
import org.bukkit.event.HandlerList

/**
 * A Bukkit event fired during plugin startup to allow other plugins to register their tools.
 *
 * This event is called by the host plugin during [org.bukkit.plugin.java.JavaPlugin.onEnable],
 * after the [ToolRegistry] has been initialized but before it is frozen. Listeners should
 * register their tools via the provided [registry] and must not retain a reference to it
 * beyond the scope of their event handler, as the registry will be frozen immediately after
 * the event returns.
 *
 * @property registry The [ToolRegistry] to register tools into.
 * @see ToolRegistry
 */
class ToolRegistrationEvent(
    val registry: ToolRegistry
): Event() {
    companion object {
        @JvmStatic
        val handlerList = HandlerList()
    }

    override fun getHandlers() = handlerList
}
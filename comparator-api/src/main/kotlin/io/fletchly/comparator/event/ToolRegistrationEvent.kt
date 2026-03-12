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

package io.fletchly.comparator.event

import io.fletchly.port.`in`.ToolRegistry
import org.bukkit.event.Event
import org.bukkit.event.HandlerList

/**
 * Event triggered to allow the registration of custom tools into the tool registry.
 *
 * This event provides access to the [ToolRegistry], enabling plugins or other components
 * to register their custom tools during the server's initialization phase. Tools added
 * to the registry are then available for execution throughout the system.
 *
 * The [ToolRegistrationEvent] is typically fired during the server's startup sequence
 * or during specific plugin initialization routines. Once the event is dispatched,
 * listeners can interact with the provided [registry] to add tools by invoking
 * the `register` method with instances of [io.fletchly.comparator.model.tool.Tool]
 *
 * @sample io.fletchly.comparator.example.ToolRegistrationExample.simpleToolRegistration
 *
 * @property registry The registry instance used to manage and register tools.
 */
class ToolRegistrationEvent(
    val registry: ToolRegistry
) : Event() {
    companion object {
        @JvmStatic
        val handlerList = HandlerList()
    }

    override fun getHandlers() = handlerList
}
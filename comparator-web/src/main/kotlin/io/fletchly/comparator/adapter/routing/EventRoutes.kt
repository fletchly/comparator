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

package io.fletchly.comparator.adapter.routing

import io.fletchly.comparator.adapter.event.EventBus
import io.fletchly.comparator.model.event.ConversationEvent
import io.fletchly.comparator.port.out.EventPort
import io.ktor.server.routing.Route
import io.ktor.server.sse.sse
import io.ktor.sse.ServerSentEvent

fun Route.eventRoutes(eventBus: EventPort) {
    sse("/events") {
        eventBus.events.collect { event ->
            when (event) {
                is ConversationEvent.MessageAdded ->
                    send(ServerSentEvent(event.key.uniqueId.toString(), event = "message"))
                is ConversationEvent.ConversationCleared ->
                    send(ServerSentEvent(event.key.uniqueId.toString(), event = "cleared"))
                ConversationEvent.AllCleared ->
                    send(ServerSentEvent("", event = "all-cleared"))
            }
        }
    }
}
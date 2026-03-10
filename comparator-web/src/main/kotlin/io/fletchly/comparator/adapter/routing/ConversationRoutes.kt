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

import io.fletchly.comparator.model.message.ChatConversationKey
import io.fletchly.comparator.model.message.ConsoleConversationKey
import io.fletchly.comparator.port.out.DataRepositoryPort
import io.ktor.http.HttpStatusCode
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.delete
import io.ktor.server.routing.get
import io.ktor.server.routing.route
import java.util.UUID

fun Route.conversationRoutes(repository: DataRepositoryPort) {
    route("/conversation") {
        get {
            call.respond(repository.getAllConversations())
        }
        delete {
            repository.clearAllConversations()
            call.respond(HttpStatusCode.NoContent)
        }

        get("/console") {
            call.respond(repository.getConversation(ConsoleConversationKey.uniqueId))
        }
        delete("/console") {
            repository.clearConversation(ConsoleConversationKey.uniqueId)
            call.respond(HttpStatusCode.NoContent)
        }

        get("/chat") {
            call.respond(repository.getConversation(ChatConversationKey.uniqueId))
        }
        delete("/chat") {
            repository.clearConversation(ChatConversationKey.uniqueId)
            call.respond(HttpStatusCode.NoContent)
        }

        get("/player") {
            call.respond(repository.getAllPlayerConversations())
        }

        get("/{id}") {
            val id = call.parameters["id"]
                ?: return@get call.respond(HttpStatusCode.BadRequest)
            val key = runCatching { UUID.fromString(id) }
                .getOrElse { return@get call.respond(HttpStatusCode.BadRequest) }
            val conversation = repository.getConversation(key)
            call.respond(conversation)
        }

        delete("/{id}") {
            val id = call.parameters["id"]
                ?: return@delete call.respond(HttpStatusCode.BadRequest)
            val key = runCatching { UUID.fromString(id) }
                .getOrElse { return@delete call.respond(HttpStatusCode.BadRequest) }
            repository.getConversation(key)
            call.respond(HttpStatusCode.NoContent)
        }
    }
}
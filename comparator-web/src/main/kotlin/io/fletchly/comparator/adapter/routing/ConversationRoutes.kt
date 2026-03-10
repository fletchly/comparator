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

import io.fletchly.comparator.port.out.DataRepositoryPort
import io.ktor.http.HttpStatusCode
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.route
import java.util.UUID

class ConversationRoutes(
    private val repository: DataRepositoryPort
) {
    fun Route.conversationRoutes() {
        route("/conversation") {
            get {
                call.respond(repository.getAllConversations())
            }
            get("/{id}") {
                val id = call.parameters["id"]
                    ?: return@get call.respond(HttpStatusCode.Companion.BadRequest)
                val uuid = runCatching { UUID.fromString(id) }
                    .getOrElse { return@get call.respond(HttpStatusCode.Companion.BadRequest) }
                val conversation = repository.getConversation(uuid)
                call.respond(conversation)
            }
        }
    }
}
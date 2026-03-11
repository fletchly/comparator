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
import io.ktor.http.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.toolRoutes(repository: DataRepositoryPort) {
    route("/tool") {
        get {
            call.respond(repository.getAllTools())
        }
        get("/{name}") {
            val name = call.parameters["name"]
                ?: return@get call.respond(HttpStatusCode.BadRequest)
            repository.getTool(name)
                .onSuccess { call.respond(it) }
                .onFailure {
                    when (it) {
                        is NoSuchElementException -> call.respond(HttpStatusCode.NotFound)
                        else -> call.respond(HttpStatusCode.InternalServerError)
                    }
                }
        }
    }
}
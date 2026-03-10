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

package io.fletchly.comparator.adapter.web

import io.fletchly.comparator.adapter.routing.conversationRoutes
import io.fletchly.comparator.adapter.routing.toolRoutes
import io.fletchly.comparator.model.options.WebPanelOptions
import io.fletchly.comparator.model.web.WebPanelMessage
import io.fletchly.comparator.port.out.DataRepositoryPort
import io.fletchly.comparator.port.out.WebPanelPort
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.install
import io.ktor.server.engine.EmbeddedServer
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.response.respondText
import io.ktor.server.routing.get
import io.ktor.server.routing.route
import io.ktor.server.routing.routing

class KtorWebPanel(
    private val options: WebPanelOptions,
    private val repository: DataRepositoryPort
) : WebPanelPort {
    private var server: EmbeddedServer<*, *>? = null
    private var isRunning = false

    override suspend fun start(): WebPanelMessage {
        if (isRunning) return WebPanelMessage.Ok("Web panel is already running on port ${options.port}")
        try {
            server = embeddedServer(Netty, port = options.port, host = options.host) {
                install(ContentNegotiation) { json() }

                routing {
                    route("/api") {
                        conversationRoutes(repository)
                        toolRoutes(repository)
                    }
                }

            }.apply {
                start(wait = false)  // non-blocking
            }
            isRunning = true
            return WebPanelMessage.Ok("Started web panel on port ${options.port}")
        } catch (ex: Exception) {
            return WebPanelMessage.Error("Failed to start web panel: ${ex.message}")
        }
    }

    override suspend fun stop(graceMs: Long, timeoutMs: Long): WebPanelMessage {
        if (!isRunning || server == null) return WebPanelMessage.Ok("The web panel is not running")

        server?.stop(graceMs, timeoutMs)
        server = null
        isRunning = false
        return WebPanelMessage.Ok("Stopped the web panel")
    }

    override suspend fun restart(onStop: suspend (WebPanelMessage) -> Unit, onStart: suspend (WebPanelMessage) -> Unit) {
        onStop.invoke(stop(1000,1000))
        onStart.invoke(start())
    }

    override suspend fun status(): WebPanelMessage = when {
        isRunning -> WebPanelMessage.Ok("The web panel is running on ${options.port}")
        else -> WebPanelMessage.Ok("The web panel is not running")
    }

    override suspend fun forceStop() {
        stop(500, 1000)
    }
}
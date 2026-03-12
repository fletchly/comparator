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
import io.fletchly.comparator.adapter.routing.eventRoutes
import io.fletchly.comparator.adapter.routing.toolRoutes
import io.fletchly.comparator.adapter.routing.utilRoutes
import io.fletchly.comparator.model.options.WebPanelOptions
import io.fletchly.comparator.model.panel.PanelMessage
import io.fletchly.comparator.port.out.DataRepositoryPort
import io.fletchly.comparator.port.out.EventPort
import io.fletchly.comparator.port.out.PanelPort
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.http.content.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.routing.*
import io.ktor.server.sse.*
import org.koin.java.KoinJavaComponent.getKoin

class KtorWebPanel(
    private val options: WebPanelOptions,
    private val eventBus: EventPort
) : PanelPort {
    private val repository: DataRepositoryPort by lazy { getKoin().get() }
    private var server: EmbeddedServer<*, *>? = null
    private var isRunning = false

    override suspend fun start(): PanelMessage {
        if (isRunning) return PanelMessage.Ok("Web panel is already running on port ${options.port}")
        try {
            server = embeddedServer(Netty, port = options.port, host = options.host) {
                install(ContentNegotiation) { json() }
                install(SSE)

                routing {
                    route("/api") {
                        utilRoutes(repository)
                        eventRoutes(eventBus)
                        conversationRoutes(repository)
                        toolRoutes(repository)
                    }

                    singlePageApplication {
                        useResources = true
                        filesPath = "web"
                        defaultPage = "index.html"
                        ignoreFiles { it.endsWith(".map") }
                    }
                }
            }.apply {
                start(wait = false)  // non-blocking
            }
            isRunning = true
            return PanelMessage.Ok("Started web panel on port ${options.port}")
        } catch (ex: Exception) {
            return PanelMessage.Error("Failed to start web panel: ${ex.message}")
        }
    }

    override suspend fun stop(graceMs: Long, timeoutMs: Long): PanelMessage {
        if (!isRunning || server == null) return PanelMessage.Ok("The web panel is not running")

        server?.stop(graceMs, timeoutMs)
        server = null
        isRunning = false
        return PanelMessage.Ok("Stopped the web panel")
    }

    override suspend fun restart(
        onStop: suspend (PanelMessage) -> Unit,
        onStart: suspend (PanelMessage) -> Unit
    ) {
        onStop.invoke(stop(1000, 1000))
        onStart.invoke(start())
    }

    override suspend fun status(): PanelMessage = when {
        isRunning -> PanelMessage.Ok("The web panel is running on ${options.port}")
        else -> PanelMessage.Ok("The web panel is not running")
    }

    override suspend fun forceStop() {
        stop(500, 1000)
    }
}
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

package io.fletchly.comparator.adapter.tool

import io.fletchly.comparator.exception.ToolException
import io.fletchly.comparator.model.options.WebSearchOptions
import io.fletchly.comparator.model.tool.Description
import io.fletchly.comparator.model.tool.ToolDefinition
import io.fletchly.comparator.model.tool.tool
import io.fletchly.comparator.port.out.LogPort
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.Url
import io.ktor.http.contentType
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement
import org.koin.dsl.bind
import org.koin.dsl.module

class WebSearchTool(
    private val config: WebSearchOptions,
    private val log: LogPort,
    private val client: HttpClient
): ToolDefinition {
    suspend fun doWebSearch(
        @Description("Web search query") query: String
    ): JsonElement {
        val webSearchRequest = WebSearchRequest(query)
        val url = Url(WEB_SEARCH_URL)

        return runCatching {
            client.post(url) {
                if (!config.apiKey.isNullOrBlank()) bearerAuth(config.apiKey)
                contentType(ContentType.Application.Json)
                setBody(webSearchRequest)
            }.body<JsonElement>()
        }.getOrElse {
            if (it is ClientRequestException && it.response.status == HttpStatusCode.Unauthorized)
                log.warn("Got 'unauthorized' response from Ollama server! Is your API key set?")
            throw ToolException("Error communicating with web search client")
        }
    }

    override val definition = tool("web_search") {
        description = "Search the web for answers"
        executes(this@WebSearchTool::doWebSearch)
    }

    companion object {
        val module = module {
            single {
                WebSearchTool(
                    get(),
                    get(),
                    io.fletchly.comparator.infra.http.HttpClient.Ktor
                )
            } bind ToolDefinition::class
        }

        private const val WEB_SEARCH_URL = "https://ollama.com/api/web_search"
    }
}

@Serializable
data class WebSearchRequest(val query: String)
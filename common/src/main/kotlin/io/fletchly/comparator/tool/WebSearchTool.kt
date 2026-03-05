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

package io.fletchly.comparator.tool

import io.fletchly.comparator.annotation.ToolFunction
import io.fletchly.comparator.annotation.ToolParameter
import io.fletchly.comparator.exception.ToolException
import io.fletchly.comparator.model.options.WebSearchOptions
import io.fletchly.comparator.port.out.LogPort
import io.fletchly.comparator.util.HttpClients
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.serialization.json.JsonElement
import org.koin.java.KoinJavaComponent.getKoin

private const val WEB_SEARCH_URL = "https://ollama.com/api/web_search"

class WebSearchTool(
    private val config: WebSearchOptions,
    private val log: LogPort,
    private val client: HttpClient = HttpClients.KtorCIO
) {
    @ToolFunction(name = "web_search", description = "search the web for answers")
    suspend fun handleWebSearch(
        @ToolParameter("query to search for") query: String
    ): JsonElement {
        val webSearchRequest = mapOf("query" to query)
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
            throw ToolException("Error communicating with web search client", it)
        }
    }
}

private val instance = getKoin().get<WebSearchTool>()
val webSearchTool = tool(instance::handleWebSearch)
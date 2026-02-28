package io.fletchly.comparator.tool.web

import io.fletchly.comparator.exception.ToolException
import io.fletchly.comparator.infra.http.HttpClient
import io.fletchly.comparator.model.tool.Description
import io.fletchly.comparator.model.tool.tool
import io.fletchly.comparator.port.out.LogPort
import io.fletchly.comparator.tool.web.dto.WebSearchRequest
import io.ktor.client.call.*
import io.ktor.client.plugins.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.serialization.json.JsonElement

class WebSearchTool(
    private val apiKey: String?,
    private val log: LogPort
) {
    private val client = HttpClient.Ktor

    private suspend fun doWebSearch(
        @Description("Web search query") query: String
    ): JsonElement {
        val webSearchRequest = WebSearchRequest(query)
        val url = Url(WEB_SEARCH_URL)

        return runCatching {
            client.post(url) {
                if (!apiKey.isNullOrBlank()) bearerAuth(apiKey)
                contentType(ContentType.Application.Json)
                setBody(webSearchRequest)
            }.body<JsonElement>()
        }.getOrElse {
            if (it is ClientRequestException && it.response.status == HttpStatusCode.Unauthorized)
                log.warn("Got 'unauthorized' response from Ollama server! Is your API key set?")
            throw ToolException("Error communicating with web search client")
        }
    }

    companion object {
        private const val WEB_SEARCH_URL = "https://ollama.com/api/web_search"

        val definition = tool("web_search") {
            description = "Search the web for answers"
            executes(WebSearchTool::doWebSearch)
        }
    }
}
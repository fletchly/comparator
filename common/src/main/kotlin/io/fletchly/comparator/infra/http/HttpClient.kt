package io.fletchly.comparator.infra.http

import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.HttpCallValidator
import io.ktor.client.plugins.HttpRequestRetry
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.RedirectResponseException
import io.ktor.client.plugins.ServerResponseException
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

object HttpClient {
    private const val REQUEST_TIMEOUT_MS: Long = 2 * 60 * 1000 // 2 minutes
    private const val CONNECT_TIMEOUT_MS: Long = 10 * 1000 // 10 seconds
    private const val SOCKET_TIMEOUT_MS: Long = 2 * 60 * 1000 // 2 minutes
    private const val MAX_RETRIES = 5
    private const val BASE_DELAY_MS = 1000L
    private const val MAX_DELAY_MS = 60_000L
    private const val RANDOMIZATION_MS = 1000L

    val ktor = HttpClient(CIO) {
        expectSuccess = true
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                encodeDefaults = true
            })
        }
        install(HttpTimeout) {
            requestTimeoutMillis = REQUEST_TIMEOUT_MS
            connectTimeoutMillis = CONNECT_TIMEOUT_MS
            socketTimeoutMillis = SOCKET_TIMEOUT_MS
        }
        install(HttpRequestRetry) {
            maxRetries = MAX_RETRIES
            retryOnServerErrors()
            retryOnException(retryOnTimeout = true)
            exponentialDelay(
                baseDelayMs = BASE_DELAY_MS,
                maxDelayMs = MAX_DELAY_MS,
                randomizationMs = RANDOMIZATION_MS
            )
        }
        install(HttpCallValidator) {
            validateResponse { response ->
                when (response.status.value) {
                    in 300..399 -> throw RedirectResponseException(response, "Redirect: ${response.status.description}")
                    in 400..499 -> throw ClientRequestException(
                        response,
                        "Client error: ${response.status.description}"
                    )
                    in 500..599 -> throw ServerResponseException(
                        response,
                        "Server error: ${response.status.description}"
                    )
                }
            }
        }
    }
}
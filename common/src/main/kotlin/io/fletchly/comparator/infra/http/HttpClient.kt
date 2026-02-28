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

package io.fletchly.comparator.infra.http

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.io.IOException
import kotlinx.serialization.json.Json
import io.ktor.client.HttpClient as KtorClient

/**
 * A singleton object for initializing and managing a configured HttpClient instance using Ktor.
 */
object HttpClient {
    private const val MAX_RETRIES = 4
    private const val REQUEST_TIMEOUT_MS = 30_000L   // 30 seconds
    private const val CONNECT_TIMEOUT_MS = 10_000L   // 10 seconds
    private const val SOCKET_TIMEOUT_MS = 30_000L    // 30 seconds
    private const val BASE_DELAY_MS = 1_000L // 1 second
    private const val MAX_DELAY_MS = 10_000L  // 10 seconds
    private const val RANDOMIZATION_MS = 500L // 0.5 seconds

    val defaultConfig: HttpClientConfig<*>.() -> Unit = {
        expectSuccess = false
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
            retryIf { _, response ->
                response.status == HttpStatusCode.ServiceUnavailable ||
                        response.status == HttpStatusCode.GatewayTimeout ||
                        response.status == HttpStatusCode.TooManyRequests
            }
            retryOnExceptionIf { _, cause ->
                cause is IOException
            }
            exponentialDelay(
                baseDelayMs = BASE_DELAY_MS,
                maxDelayMs = MAX_DELAY_MS,
                randomizationMs = RANDOMIZATION_MS
            )
        }
        install(HttpCallValidator) {
            validateResponse { response ->
                when (response.status.value) {
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

    val Ktor = KtorClient(CIO, defaultConfig)
}
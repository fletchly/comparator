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

package io.fletchly.comparator.di

import io.fletchly.comparator.adapter.config.PluginConfigService
import io.fletchly.comparator.adapter.config.SystemPromptService
import io.fletchly.comparator.model.options.*
import io.fletchly.comparator.port.out.SystemConfigPort
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val paperConfigModule = module {
    singleOf(::PluginConfigService)
    singleOf(::SystemPromptService) bind SystemConfigPort::class

    single {
        val ollamaConfig = get<PluginConfigService>().config.aiProvider.ollama
        OllamaOptions(
            ollamaConfig.baseUrl,
            ollamaConfig.apiKey,
            ollamaConfig.model
        )
    }

    single {
        val contextConfig = get<PluginConfigService>().config.context
        ContextOptions(
            contextConfig.conversationMessageLimit,
            contextConfig.expireAfterAccessMinutes
        )
    }

    single {
        val config = get<PluginConfigService>().config
        WebSearchOptions(
            config.tool.webSearch.apiKey ?: config.aiProvider.ollama.apiKey
        )
    }

    single {
        val config = get<PluginConfigService>().config

        PublicChatPrefixOptions(
            config.publicChatPrefix
        )
    }

    single {
        val config = get<PluginConfigService>().config.tool

        BuiltInToolOptions(
            config.webSearch.enabled,
            config.gameVersion.enabled,
            config.currentDate.enabled
        )
    }
}
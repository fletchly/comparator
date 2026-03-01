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

package io.fletchly.comparator.model.config

import org.spongepowered.configurate.objectmapping.ConfigSerializable
import org.spongepowered.configurate.objectmapping.meta.Comment
import org.spongepowered.configurate.objectmapping.meta.Setting

@ConfigSerializable
data class PluginConfig(
    @Comment("Options for the context store")
    val context: ContextConfig = ContextConfig(),

    @Setting("ai-provider")
    @Comment("AI provider options")
    val aiProvider: AIProviderConfig = AIProviderConfig(),

    @Comment("Tool options")
    val tool : ToolConfig = ToolConfig()
)

@ConfigSerializable
data class ContextConfig(
    @Setting("conversation-message-limit")
    @Comment("Maximum messages allowed in context per-player")
    val conversationMessageLimit: Int = 25
)

@ConfigSerializable
data class AIProviderConfig(
    @Comment("Configuration for the Ollama AI provider")
    val ollama: OllamaConfig = OllamaConfig()
)

@ConfigSerializable
data class OllamaConfig(
    @Setting("base-url")
    @Comment("The base URL of the Ollama instance.")
    val baseUrl: String = "https://ollama.com",

    @Setting("api-key")
    @Comment("Your key for the Ollama cloud API. This only needs to be set if you are using an Ollama cloud model.")
    val apiKey: String? = "",

    @Comment("The identifier of the model to use for response generation")
    val model: String = "deepseek-v3.1:671b"
)

@ConfigSerializable
data class ToolConfig(
    @Setting("web-search")
    @Comment("Web search tool options")
    val webSearch: WebSearchConfig = WebSearchConfig()
)

@ConfigSerializable
data class WebSearchConfig(
    @Comment("Enable web search")
    override val enabled: Boolean = true,

    @Setting("api-key")
    @Comment("Ollama Cloud API key. Will use ai-provider.ollama.api-key if left blank")
    val apiKey: String? = ""
) : ToolImplementationConfig

interface ToolImplementationConfig {
    val enabled: Boolean
}
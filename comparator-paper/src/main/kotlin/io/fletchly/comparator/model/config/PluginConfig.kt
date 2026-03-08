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
    @Setting("public-chat-prefix")
    @Comment("The prefix used to invoke the assistant in public chat (e.g. \"@bot Hello!\")")
    val publicChatPrefix: String = "@bot",

    @Comment("Options for conversation context storage")
    val context: ContextConfig = ContextConfig(),

    @Setting("ai-provider")
    @Comment("Options for the AI provider")
    val aiProvider: AIProviderConfig = AIProviderConfig(),

    @Comment("Options for assistant tools")
    val tool: ToolConfig = ToolConfig(),

    @Setting(ConfigLoader.VERSION_KEY)
    @Comment("Config version - do not modify this value, as it may cause your config to be overwritten")
    val version: Int = 3
)

@ConfigSerializable
data class ContextConfig(
    @Setting("conversation-message-limit")
    @Comment("Maximum number of messages to retain in context per player")
    val conversationMessageLimit: Int = 25,

    @Setting("expire-after-access-minutes")
    @Comment("Number of minutes to retain a player's context after their conversation goes inactive")
    val expireAfterAccessMinutes: Long = 10
)

@ConfigSerializable
data class AIProviderConfig(
    @Comment("Options for the Ollama AI provider")
    val ollama: OllamaConfig = OllamaConfig()
)

@ConfigSerializable
data class OllamaConfig(
    @Setting("base-url")
    @Comment("The base URL of the Ollama instance")
    val baseUrl: String = "https://ollama.com",

    @Setting("api-key")
    @Comment("API key for the Ollama API - only required when using an Ollama cloud model")
    val apiKey: String? = "",

    @Comment("The model to use for response generation")
    val model: String = "qwen3.5:397b-cloud"
)

@ConfigSerializable
data class ToolConfig(
    @Setting("web-search")
    @Comment("Options for the web search tool")
    val webSearch: WebSearchConfig = WebSearchConfig(),

    @Setting("game-version")
    @Comment("Options for the game version tool")
    val gameVersion: GameVersionConfig = GameVersionConfig(),

    @Setting("current-date")
    @Comment("Options for the current date tool")
    val currentDate: CurrentDateConfig = CurrentDateConfig()
)

@ConfigSerializable
data class WebSearchConfig(
    @Comment("Whether the web search tool is enabled")
    override val enabled: Boolean = true,

    @Setting("api-key")
    @Comment("API key for the web search tool - falls back to ai-provider.ollama.api-key if left blank")
    val apiKey: String? = ""
) : ToolImplementationConfig

@ConfigSerializable
data class GameVersionConfig(
    @Comment("Whether the game version tool is enabled")
    override val enabled: Boolean = true
) : ToolImplementationConfig

@ConfigSerializable
data class CurrentDateConfig(
    @Comment("Whether the current date tool is enabled")
    override val enabled: Boolean = true
) : ToolImplementationConfig

interface ToolImplementationConfig {
    val enabled: Boolean
}
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
    @Comment("The prefix used to invoke the assistant in public chat")
    val publicChatPrefix: String = "@bot",

    @Comment("Options for the context store")
    val context: ContextConfig = ContextConfig(),

    @Setting("ai-provider")
    @Comment("AI provider options")
    val aiProvider: AIProviderConfig = AIProviderConfig(),

    @Comment("Tool options")
    val tool: ToolConfig = ToolConfig(),

    @Setting(ConfigLoader.VERSION_KEY)
    @Comment("Don't change this. Doing so could overwrite existing config")
    val version: Int = 0
) {
    companion object {
        val Default = PluginConfig()
    }
}

@ConfigSerializable
data class ContextConfig(
    @Setting("conversation-message-limit")
    @Comment("Maximum messages allowed in context per-player")
    val conversationMessageLimit: Int = 25,

    @Setting("expire-after-access-minutes")
    @Comment("Maximum time (in minutes) to keep context for after conversation goes inactive")
    val expireAfterAccessMinutes: Long = 10
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
    val webSearch: WebSearchConfig = WebSearchConfig(),

    @Setting("game-version")
    @Comment("Game version tool options")
    val gameVersion: GameVersionConfig = GameVersionConfig()
)

@ConfigSerializable
data class WebSearchConfig(
    @Comment("Enable web search")
    override val enabled: Boolean = true,

    @Setting("api-key")
    @Comment("Ollama Cloud API key. Will use ai-provider.ollama.api-key if left blank")
    val apiKey: String? = ""
) : ToolImplementationConfig

@ConfigSerializable
data class GameVersionConfig(
    @Comment("Enable game version check")
    override val enabled: Boolean = true
) : ToolImplementationConfig

interface ToolImplementationConfig {
    val enabled: Boolean
}
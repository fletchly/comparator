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

@ConfigSerializable
data class SystemPromptConfig(
    @Comment("Set of instructions given to the AI model before each conversation, defining its behavior, persona, and constraints.")
    val prompt: String = DEFAULT_PROMPT
) {
    companion object {
        val DEFAULT_PROMPT = """
            You are a helpful assistant specializing in Minecraft: Java Edition. 
            Your purpose is to provide accurate, practical information about gameplay, 
            mechanics, crafting, building, survival strategies, and game features to 
            players while they're in-game.
        """.trimIndent().replace("\n", "")
    }
}

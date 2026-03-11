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

package io.fletchly.comparator.model.dto

import io.fletchly.comparator.model.event.ToolEvent
import io.fletchly.comparator.util.toJsonObject
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement
import java.util.UUID

@Serializable
data class ToolExecutedDto(
    val args: Map<String, JsonElement>,
    val result: String,
    val name: String,
    @SerialName("conversation_id")
    val conversationId: String
) {
}

fun ToolEvent.ToolExecuted.toDto() = ToolExecutedDto (
    args = this.args.toJsonObject(),
    result = this.result.content,
    name = this.result.name,
    conversationId = this.conversationKey.uniqueId.toString()
)
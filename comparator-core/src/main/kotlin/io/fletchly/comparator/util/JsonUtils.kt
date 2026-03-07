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

package io.fletchly.comparator.util

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.*

/**
 * Converts this map to a JSON object, with each key-value pair being
 * transformed into a corresponding JSON representation. The map values
 * are recursively converted to JSON elements using the `toJsonElement` function.
 *
 * @return A [JsonObject] representing the serialized structure of this map.
 */
fun Map<String, Any>.toJsonObject(): JsonObject {
    return JsonObject(mapValues { (_, value) -> value.toJsonElement() })
}

/**
 * Converts this object to a [JsonElement].
 *
 * @return A [JsonElement] representation of this object.
 */
fun Any?.toJsonElement(): JsonElement = when (this) {
    is Boolean -> JsonPrimitive(this)
    is Number -> JsonPrimitive(this)
    is String -> JsonPrimitive(this)
    is Map<*, *> -> JsonObject(
        this.entries.associate { (k, v) ->
            k.toString() to v.toJsonElement()
        }
    )

    is List<*> -> JsonArray(this.map { it.toJsonElement() })
    is Array<*> -> JsonArray(this.map { it.toJsonElement() })
    else -> JsonPrimitive(this.toString()) // fallback
}

fun JsonObject.toMap(): Map<String, Any> {
    return mapValues { (_, value) -> value.toAny() }
}

fun JsonElement.toAny(): Any = when (this) {
    is JsonPrimitive -> toAnyPrimitive()
    is JsonObject -> toMap()
    is JsonArray -> this.map { it.toAny() }
}

fun JsonPrimitive.toAnyPrimitive(): Any {
    // kotlinx.serialization stores everything as a string internally,
    // so we try to parse in order of specificity
    booleanOrNull?.let { return it }
    intOrNull?.let { return it }
    longOrNull?.let { return it }
    doubleOrNull?.let { return it }
    return content // fallback to String
}

@Serializable
data class JsonSchema(
    val type: String,
    val properties: Map<String, JsonProperty>,
    val required: List<String>
)

@Serializable
data class JsonProperty(
    val type: String,
    val description: String
)
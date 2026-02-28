package io.fletchly.comparator.util

import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive

fun Map<String, Any?>.toJsonObject(): JsonObject {
    return JsonObject(mapValues { (_, value) -> value.toJsonElement() })
}

fun Any?.toJsonElement(): JsonElement = when (this) {
    null          -> JsonNull
    is Boolean    -> JsonPrimitive(this)
    is Number     -> JsonPrimitive(this)
    is String     -> JsonPrimitive(this)
    is Map<*, *>  -> (this as Map<String, Any?>).toJsonObject()
    is List<*>    -> JsonArray(this.map { it.toJsonElement() })
    is Array<*>   -> JsonArray(this.map { it.toJsonElement() })
    else          -> JsonPrimitive(this.toString()) // fallback
}
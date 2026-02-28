package io.fletchly.comparator.util

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.booleanOrNull
import kotlinx.serialization.json.doubleOrNull
import kotlinx.serialization.json.intOrNull
import kotlinx.serialization.json.longOrNull

fun Map<String, Any>.toJsonObject(): JsonObject {
    return JsonObject(mapValues { (_, value) -> value.toJsonElement() })
}

fun Any?.toJsonElement(): JsonElement = when (this) {
    is Boolean    -> JsonPrimitive(this)
    is Number     -> JsonPrimitive(this)
    is String     -> JsonPrimitive(this)
    is Map<*, *> -> JsonObject(
        this.entries.associate { (k, v) ->
            k.toString() to v.toJsonElement()
        }
    )
    is List<*>    -> JsonArray(this.map { it.toJsonElement() })
    is Array<*>   -> JsonArray(this.map { it.toJsonElement() })
    else          -> JsonPrimitive(this.toString()) // fallback
}

fun JsonObject.toMap(): Map<String, Any> {
    return mapValues { (_, value) -> value.toAny() }
}

fun JsonElement.toAny(): Any = when (this) {
    is JsonPrimitive -> toAnyPrimitive()
    is JsonObject    -> toMap()
    is JsonArray     -> this.map { it.toAny() }
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
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

package io.fletchly.comparator.model.tool

import kotlin.reflect.KType

data class Parameter(
    val name: String,
    val type: Type,
    val description: String,
    val required: Boolean = true,
    val enum: List<String>? = null,
    val elementType: Type? = null
) {
    enum class Type { STRING, INTEGER, NUMBER, BOOLEAN, ARRAY }

    init {
        require(type != Type.ARRAY || elementType != null) {
            "elementType must be provided for ARRAY parameters"
        }
        require(elementType != Type.ARRAY) {
            "Nested arrays are not supported"
        }
    }
}

/**
 * Converts a [KType] to a pair of [Parameter.Type] representations.
 *
 * If the [KType] corresponds to a `List`, this method extracts the element type
 * and ensures it is not another `List`, as nested arrays are not supported.
 * For non-collection types, it maps the classifier to an appropriate
 * [Parameter.Type].
 *
 * @return A pair consisting of the main [Parameter.Type] and an optional
 * element type if the main type is an `ARRAY`. The second value in the pair is
 * `null` when the type is not an `ARRAY`.
 * @throws IllegalArgumentException If the provided type has an unsupported classifier
 * or if a `List` does not specify a type argument.
 */
fun KType.toToolParameterType(): Pair<Parameter.Type, Parameter.Type?> {
    if (classifier == List::class) {
        val elementKType = arguments.firstOrNull()?.type
            ?: error("List parameter must have a type argument")
        val elementType = elementKType.toToolParameterType().first
        require(elementType != Parameter.Type.ARRAY) {
            "Nested arrays are not supported"
        }
        return Parameter.Type.ARRAY to elementType
    }

    val type = when (classifier) {
        String::class -> Parameter.Type.STRING
        Int::class, Long::class -> Parameter.Type.INTEGER
        Float::class, Double::class -> Parameter.Type.NUMBER
        Boolean::class -> Parameter.Type.BOOLEAN
        else -> error("Unsupported parameter type: $classifier")
    }
    return type to null
}
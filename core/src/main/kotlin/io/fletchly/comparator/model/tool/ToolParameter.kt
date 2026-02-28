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

/**
 * Represents a single parameter required by a tool.
 *
 * This class defines the structure and characteristics of a parameter, including its name, type,
 * description, whether it is required, and an optional list of enumerated values it can take.
 *
 * @property name The unique name of the parameter.
 * @property type The data type of the parameter, defined by the [Type] enum.
 * @property description A brief description of the parameter, detailing its purpose or usage.
 * @property required Indicates whether this parameter is mandatory. Defaults to true.
 * @property enum An optional list of valid values the parameter can take. This is null if the parameter does not have enumerated values.
 */
data class ToolParameter(
    val name: String,
    val type: Type,
    val description: String,
    val required: Boolean = true,
    val enum: List<String>? = null
) {
    enum class Type { STRING, INTEGER, NUMBER, BOOLEAN, OBJECT }
}

/**
 * Converts the current [KType] instance to the corresponding [ToolParameter.Type].
 *
 * This function maps Kotlin types to their respective tool parameter types
 * defined in the [ToolParameter.Type] enum. The mapping is as follows:
 * - `String` maps to `ToolParameter.Type.STRING`
 * - `Int` or `Long` maps to `ToolParameter.Type.INTEGER`
 * - `Float` or `Double` maps to `ToolParameter.Type.NUMBER`
 * - `Boolean` maps to `ToolParameter.Type.BOOLEAN`
 * - Any other type maps to `ToolParameter.Type.OBJECT`
 *
 * @return The [ToolParameter.Type] corresponding to the [KType].
 */
fun KType.toToolParameterType(): ToolParameter.Type = when (classifier) {
    String::class -> ToolParameter.Type.STRING
    Int::class, Long::class -> ToolParameter.Type.INTEGER
    Float::class, Double::class -> ToolParameter.Type.NUMBER
    Boolean::class -> ToolParameter.Type.BOOLEAN
    else -> ToolParameter.Type.OBJECT
}
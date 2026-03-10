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

import io.fletchly.comparator.model.tool.Parameter
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ParameterDto(
    val name: String,
    val type: String,
    val description: String,
    val required: Boolean,
    val enum: List<String>?,
    @SerialName("element-type")
    val elementType: String?
)

fun Parameter.toDto() = ParameterDto(
    name = this.name,
    type = this.type.name.lowercase(),
    description = this.description,
    required = this.required,
    enum = this.enum,
    elementType = this.elementType?.name?.lowercase()
)

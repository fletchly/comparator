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

package io.fletchly.comparator.annotation

/**
 * Annotation for defining metadata for a tool function parameter.
 *
 * This annotation is intended to provide descriptive information and constraints for a parameter
 * of a function annotated with [ToolFunction]. It serves as a mechanism to define behavior
 * and documentation for the parameter within a tool-based system.
 *
 * @property description A textual description of the parameter's purpose or role in the function.
 * @property required Indicates whether this parameter is mandatory. Defaults to `true`.
 */
@Target(AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
annotation class ToolParameter(
    val description: String,
    val required: Boolean = true
)

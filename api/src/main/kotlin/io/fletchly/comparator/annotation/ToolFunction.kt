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
 * Annotation used to mark a function as a tool function.
 *
 * Tool functions serve as callable operations with defined names and descriptions, which can
 * then be registered and executed within the context of a tool-based system. The annotation
 * provides metadata to identify the function and document its purpose within the system.
 *
 * @property name The name of the tool function. Defaults to an empty string. If left empty,
 * the system may use the annotated function's name as a fallback.
 * @property description A detailed description of the tool function's purpose and functionality.
 */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class ToolFunction(
    val name: String = "",
    val description: String
)

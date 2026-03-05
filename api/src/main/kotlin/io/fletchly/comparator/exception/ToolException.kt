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

package io.fletchly.comparator.exception

/**
 * Represents an exception that occurs during tool processing or execution.
 *
 * This exception is used to signify errors that happen when a tool is interacting
 * with its inputs, outputs, or defined behavior. It serves as a general exception
 * for tool-related runtime issues.
 *
 * @constructor Creates a [ToolException] with a specific error message.
 * @param message A detailed message describing the reason for the exception.
 */
class ToolException(override val message: String) : Exception(message)

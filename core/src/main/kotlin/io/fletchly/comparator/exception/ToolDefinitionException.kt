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
 * Represents an exception specific to tool definition issues.
 *
 * This exception is thrown when errors related to tool definitions are encountered,
 * such as invalid or non-serializable return types in the construction or validation
 * of tools.
 *
 * @constructor Creates a [ToolDefinitionException] with a specified error message and an optional cause.
 * @param message A detailed message describing the reason for the exception.
 * @param cause The underlying cause of the exception
 */
class ToolDefinitionException(message: String, cause: Throwable? = null) : Exception(message, cause)

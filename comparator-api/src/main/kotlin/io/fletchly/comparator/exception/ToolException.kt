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
 * Represents a recoverable, model-facing error encountered during the execution of a tool.
 *
 * This exception is used to signal to the model that an error occurred during tool execution,
 * so it should be descriptive in a way that is understandable to the model.
 *
 * @constructor Creates a [ToolException] with a specified error message and an optional cause.
 * @param message A message describing the error encountered.
 * @param cause The underlying cause of the error, if any.
 */
class ToolException(override val message: String, override val cause: Throwable?) : Exception()

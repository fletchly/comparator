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

package io.fletchly.comparator.model.config

/**
 * Represents the result of an operation involving configuration loading, encapsulating either
 * a successful outcome with associated configuration data or a failure with an error message.
 *
 * This interface facilitates error handling and result propagation in a type-safe manner by using
 * its sealed subclasses to categorize outcomes as `Success` or `Failure`.
 *
 * @param T The type of the configuration data wrapped in a successful result.
 */
sealed interface ConfigResult<T> {
    class Success<T>(val config: T): ConfigResult<T>
    class Failure(val error: String): ConfigResult<Nothing>
}
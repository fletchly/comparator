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

package io.fletchly.comparator.port.out

/**
 * Provides methods for logging informational and warning messages within the system.
 */
interface LogPort {
    /**
     * Logs an informational message, optionally associated with a specific source.
     *
     * @param message The informational message to be logged.
     * @param source The source of the message, which can be used to categorize or identify the origin of the log entry. Defaults to null if not specified.
     */
    fun info(message: String, source: String? = null)

    /**
     * Logs a warning message, optionally associated with a specific source.
     *
     * @param message The warning message to be logged.
     * @param source The source of the warning, which can be used to categorize or identify the origin of the log entry. Defaults to null if not specified.
     */
    fun warn(message: String, source: String? = null)
}
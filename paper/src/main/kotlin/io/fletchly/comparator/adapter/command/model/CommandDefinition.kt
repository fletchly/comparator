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

package io.fletchly.comparator.adapter.command.model

/**
 * Defines a contract for a command's metadata and structure within the system.
 *
 * This interface acts as a wrapper for a command definition created using the Brigadier library.
 * It provides access to the underlying BrigadierCommand instance, which contains details about the command's
 * structure, permissions, and descriptive metadata.
 *
 * @property definition The BrigadierCommand instance representing the command's structure and metadata.
 */
interface CommandDefinition {
    val definition: BrigadierCommand
}
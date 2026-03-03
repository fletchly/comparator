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

package io.fletchly.comparator.model.user

/**
 * Represents a user with the ability to execute commands within the system.
 *
 * This interface extends the `User` interface and is implemented by entities
 * that are capable of issuing commands, such as a console user or a player in a game.
 *
 * It provides a common contract for distinguishing users that have command-sending
 * capabilities from those that do not.
 */
interface CommandSendingUser: User
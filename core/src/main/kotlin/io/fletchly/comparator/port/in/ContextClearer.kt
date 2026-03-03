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

package io.fletchly.comparator.port.`in`

import io.fletchly.comparator.model.user.ExecutingUser
import io.fletchly.comparator.model.user.User

/**
 * Provides an interface for clearing conversation context for one or more actors
 * in a conversational system.
 */
interface ContextClearer {


    /**
     * Clears the conversational context associated with a user.
     */
    suspend fun ExecutingUser.clearSelf()

    /**
     * Clears the conversational context for a specified list of users.
     *
     */
    suspend fun ExecutingUser.clearOther(targets: List<User>)

    /**
     * Clears all conversational contexts within the system.
     */
    suspend fun clearAll()
}
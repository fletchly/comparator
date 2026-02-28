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

package io.fletchly.comparator.core.manager

import io.fletchly.comparator.manager.ContextManager
import io.fletchly.comparator.model.user.User
import io.fletchly.comparator.port.out.ContextPort
import io.fletchly.comparator.port.out.NotificationPort
import io.mockk.called
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

class ContextManagerTest {
    private val context = mockk<ContextPort>(relaxed = true)
    private val notification = mockk<NotificationPort>(relaxed = true)
    private val manager = ContextManager(context, notification)

    @Test
    fun `clear calls context clear for each target`() = runTest {
        val users = arrayOf(mockk<User>(), mockk<User>())
        manager.clear(*users)
        users.forEach { coVerify { context.clear(it) } }
    }

    @Test
    fun `clear does not send notification`() = runTest {
        manager.clear(mockk<User>())
        verify { notification wasNot called }
    }

    @Test
    fun `clearWithFeedback sends a singular notification for one target`() = runTest {
        val sender = mockk<User>()
        manager.clearWithFeedback(sender, mockk<User>())
        coVerify { notification.info(sender, "Cleared context for 1 player") }
    }

    @Test
    fun `clearWithFeedback sends a plural notification for multiple targets`() = runTest {
        val sender = mockk<User>()
        manager.clearWithFeedback(sender, mockk<User>(), mockk<User>(), mockk<User>())
        coVerify { notification.info(sender, "Cleared context for 3 players") }
    }

    @Test
    fun `clearAll delegates to context`() = runTest {
        manager.clearAll()
        coVerify { context.clearAll() }
    }
}
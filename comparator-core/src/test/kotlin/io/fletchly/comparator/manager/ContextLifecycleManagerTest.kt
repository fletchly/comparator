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

package io.fletchly.comparator.manager

import io.fletchly.comparator.model.actor.Actor
import io.fletchly.comparator.model.message.ConversationKey
import io.fletchly.comparator.port.out.ContextPort
import io.fletchly.comparator.port.out.EventPort
import io.fletchly.comparator.port.out.LogPort
import io.fletchly.comparator.port.out.NotificationPort
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.test.runTest
import java.util.UUID
import kotlin.test.Test

class ContextLifecycleManagerTest {
    private val context = mockk<ContextPort>(relaxed = true)
    private val notification = mockk<NotificationPort>(relaxed = true)
    private val log = mockk<LogPort>(relaxed = true)
    private val event = mockk<EventPort>(relaxed = true)
    private val manager = ContextLifecycleManager(context, notification, log, event)

    private val conversationKey = mockk<ConversationKey> {
        every { uniqueId } returns UUID.randomUUID()
    }
    private val requestor = mockk<Actor>(relaxed = true) {
        every { displayName } returns "Requestor"
        every { conversationKey } returns this@ContextLifecycleManagerTest.conversationKey
    }

    // --- clearSelf ---

    @Test
    fun `clearSelf clears context for target`() = runTest {
        manager.clearSelf(requestor)
        coVerify { context.clear(conversationKey) }
    }

    @Test
    fun `clearSelf sends notification to target`() = runTest {
        manager.clearSelf(requestor)
        coVerify { notification.info(requestor, any()) }
    }

    @Test
    fun `clearSelf logs message with correct display name`() = runTest {
        manager.clearSelf(requestor)
        verify { log.info(match { it.contains("Requestor") }, any()) }
    }

    // --- clearOther ---

    @Test
    fun `clearOther clears context for each target`() = runTest {
        val target1 = mockk<ConversationKey>()
        val target2 = mockk<ConversationKey>()
        manager.clearOther(requestor, listOf(target1, target2))
        coVerify { context.clear(target1) }
        coVerify { context.clear(target2) }
    }

    @Test
    fun `clearOther sends singular notification for one target`() = runTest {
        manager.clearOther(requestor, listOf(mockk<ConversationKey>()))
        coVerify { notification.info(requestor, "Cleared chat context for 1 target") }
    }

    @Test
    fun `clearOther sends singular log message for one target`() = runTest {
        manager.clearOther(requestor, listOf(mockk<ConversationKey>()))
        verify { log.info(match { it.contains("Cleared chat context for 1 target") }, any()) }
    }

    @Test
    fun `clearOther sends plural notification for multiple targets`() = runTest {
        manager.clearOther(requestor, listOf(mockk(), mockk(), mockk()))
        coVerify { notification.info(requestor, "Cleared chat context for 3 targets") }
    }

    @Test
    fun `clearOther sends plural log message for multiple targets`() = runTest {
        manager.clearOther(requestor, listOf(mockk(), mockk(), mockk()))
        verify { log.info(match { it.contains("Cleared chat context for 3 targets") }, any()) }
    }

    // --- clearAll(requestor) ---

    @Test
    fun `clearAll with requestor delegates to context`() = runTest {
        manager.clearAll(requestor)
        coVerify { context.clearAll() }
    }

    @Test
    fun `clearAll with requestor sends notification`() = runTest {
        manager.clearAll(requestor)
        coVerify { notification.info(requestor, any()) }
    }

    @Test
    fun `clearAll with requestor sends log message`() = runTest {
        manager.clearAll(requestor)
        verify { log.info(any(), any()) }
    }

    // --- clearFull ---

    @Test
    fun `clearFull delegates to context`() = runTest {
        manager.clearFull()
        coVerify { context.clearAll() }
    }
}
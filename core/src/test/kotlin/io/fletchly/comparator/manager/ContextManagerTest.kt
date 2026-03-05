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

import io.fletchly.comparator.model.scope.ConversationScope
import io.fletchly.comparator.model.scope.RestrictedConversationScope
import io.fletchly.comparator.port.out.ContextPort
import io.fletchly.comparator.port.out.LogPort
import io.fletchly.comparator.port.out.NotificationPort
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

class ContextManagerTest {
    private val context = mockk<ContextPort>(relaxed = true)
    private val notification = mockk<NotificationPort>(relaxed = true)
    private val log = mockk<LogPort>(relaxed = true)
    private val manager = ContextManager(context, notification, log)
    private val sender = mockk<RestrictedConversationScope>(relaxed = true) {
        every { displayName } returns "Sender"
    }

    @Test
    fun `clearSelf clears context for executing user`() = runTest {
        with(manager) { sender.clearSelf() }
        coVerify { context.clear(sender) }
    }

    @Test
    fun `clearSelf sends notification`() = runTest {
        with(manager) { sender.clearSelf() }
        coVerify { notification.info(sender, any()) }
    }

    @Test
    fun `clearSelf sends log message with correct display name`() = runTest {
        with(manager) { sender.clearSelf() }
        verify { log.info(match { it.contains("Sender") }, any()) }
    }

    @Test
    fun `clearOther sends a singular notification for one target`() = runTest {
        with(manager) { sender.clearOther(listOf(mockk<ConversationScope>())) }
        coVerify { notification.info(sender, "Cleared chat context for 1 target") }
    }

    @Test
    fun `clearOther sends a singular log message for one target`() = runTest {
        with(manager) { sender.clearOther(listOf(mockk<ConversationScope>())) }
        coVerify { log.info(match { it.contains("Cleared chat context for 1 target") }, any()) }
    }

    @Test
    fun `clearOther sends a plural notification for multiple targets`() = runTest {
        with(manager) {
            sender.clearOther(
                listOf(
                    mockk<ConversationScope>(),
                    mockk<ConversationScope>(),
                    mockk<ConversationScope>()
                )
            )
        }
        coVerify { notification.info(sender, "Cleared chat context for 3 targets") }
    }

    @Test
    fun `clearOther sends a plural log message for multiple targets`() = runTest {
        with(manager) {
            sender.clearOther(
                listOf(
                    mockk<ConversationScope>(),
                    mockk<ConversationScope>(),
                    mockk<ConversationScope>()
                )
            )
        }
        coVerify { log.info(match { it.contains("Cleared chat context for 3 targets") }, any()) }
    }

    @Test
    fun `extension clearAll delegates to context`() = runTest {
        with(manager) { sender.clearAll() }
        coVerify { context.clearAll() }
    }

    @Test
    fun `extension clearAll sends notification`() = runTest {
        with(manager) { sender.clearAll() }
        coVerify { notification.info(sender, any()) }
    }

    @Test
    fun `extension clearAll sends log message`() = runTest {
        with(manager) { sender.clearAll() }
        coVerify { log.info(any(), any()) }
    }

    @Test
    fun `clearAll delegates to context`() = runTest {
        manager.clearAll()
        coVerify { context.clearAll() }
    }
}